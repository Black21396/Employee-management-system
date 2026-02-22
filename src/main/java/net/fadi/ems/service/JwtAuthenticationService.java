package net.fadi.ems.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.fadi.ems.entity.User;
import net.fadi.ems.exception.LoginException;
import net.fadi.ems.exception.RegisterException;
import net.fadi.ems.exception.UsernameOrEmailNotFoundException;
import net.fadi.ems.model.AuthenticationResponse;
import net.fadi.ems.model.LoginRequest;
import net.fadi.ems.model.RegisterRequest;
import net.fadi.ems.model.Role;
import net.fadi.ems.repository.UserRepository;
import net.fadi.ems.service.interfaces.AuthenticationServiceInterface;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService implements AuthenticationServiceInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Registriert einen neuen User.
     *
     * Schritte:
     * 1. Validierung: Email und Username einzigartig?
     * 2. Passwort hashen
     * 3. User in DB speichern
     * 4. JWT Token generieren
     * 5. Token zurückgeben
     *
     * @Transactional → DB-Operationen werden in einer Transaktion ausgeführt.
     *                Wenn etwas schiefgeht (Exception), wird automatisch ein
     *                Rollback durchgeführt.
     *                Der User wird nicht in der DB gespeichert wenn danach eine
     *                Exception fliegt.
     *
     * @param request Die Registrierungsdaten vom Frontend
     * @return AuthResponse mit JWT Token und Rolle
     */
    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {

        // ================================================================
        // SCHRITT 1: Validierung – Ist Email/Username noch verfügbar?
        // ================================================================
        /*
         * Warum prüfen wir hier, wenn die DB schon UNIQUE Constraint hat?
         *
         * Die DB würde eine DataIntegrityViolationException werfen.
         * Diese Exception hat eine generische Fehlermeldung die nichts Nützliches sagt.
         * 
         * Mit dieser Prüfung geben wir eine klare, verständliche Fehlermeldung zurück:
         * "Email bereits vergeben" → Frontend kann das anzeigen!
         *
         * Die DB-Constraint ist die letzte Sicherheitsebene (Defense in Depth).
         */
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RegisterException("Email is already taken"); // GLEICHE Fehlermeldung wie bei Username!
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RegisterException("Username is already taken"); // GLEICHE Fehlermeldung wie bei Email!
        }

        // ================================================================
        // SCHRITT 2: Passwort hashen
        // ================================================================
        /*
         * passwordEncoder.encode(rawPassword) → BCrypt-Hash erstellen
         *
         * Was BCrypt macht:
         * 1. Zufälliges "Salt" generieren (16 Bytes)
         * 2. Salt + Passwort durch 1024 Iterationen von SHA-256 jagen
         * 3. Ergebnis als String: "$2a$10$[Salt][Hash]"
         *
         * Beispiel:
         * Input: "geheim123"
         * Output: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
         *
         * Das Schöne: BCrypt.matches() weiß welches Salt genutzt wurde (es steckt im
         * Hash)
         * und kann den Vergleich korrekt durchführen!
         */
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // ================================================================
        // SCHRITT 3: User erstellen und in DB speichern
        // ================================================================
        /*
         * Builder-Pattern (Lombok @Builder):
         * Statt: new User(null, email, username, password, role)
         * Jetzt: User.builder().email(...).username(...).build()
         *
         * Vorteil: Klarer lesbar, kein Verwirren der Reihenfolge der Parameter
         */
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(hashedPassword) // ← GEHASHT! Nie das Original speichern!
                /*
                 * Rolle setzen:
                 * Wenn keine Rolle mitgeschickt wurde → Standard: USER
                 *
                 * SECURITY-HINWEIS:
                 * In Produktionsanwendungen sollte man hier immer USER setzen
                 * und einen separaten gesicherten Endpoint für Admin-Zuweisung haben!
                 *
                 * Beispiel:
                 * .role(Role.USER) // ← Immer USER, egal was gesendet wurde
                 */
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .build();

        // userRepository.save() → INSERT INTO users (...) VALUES (...)
        // Gibt den gespeicherten User zurück (mit generierter ID!)
        User savedUser = userRepository.save(user);

        // ================================================================
        // SCHRITT 4 & 5: JWT Token generieren und zurückgeben
        // ================================================================
        String token = jwtService.generateToken(savedUser);

        return new AuthenticationResponse(token, savedUser.getRole().name());
    }

    /**
     * Authentifiziert einen User (Login).
     *
     * Schritte:
     * 1. User per Email oder Username in DB suchen
     * 2. Passwort vergleichen (BCrypt)
     * 3. JWT Token generieren
     * 4. Token zurückgeben
     *
     * @param request Login-Daten (email oder username + passwort)
     * @return AuthenticationResponse mit JWT Token und Rolle
     */
    @Override
    public AuthenticationResponse login(LoginRequest request) {

        // ================================================================
        // SCHRITT 1: User finden (per Email oder Username)
        // ================================================================
        /*
         * Validierung: Hat der User überhaupt email oder username angegeben?
         */
        if (request.getEmail() == null && request.getUsername() == null) {
            throw new LoginException("Either email or username must be provided");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new LoginException("Password must be provided");
        }

        /*
         * Den "Identifier" bestimmen:
         * Hat der User eine Email angegeben? → nimm Email
         * Sonst → nimm Username
         */
        String identifier = request.getEmail() != null
                ? request.getEmail()
                : request.getUsername();

        /*
         * User in DB suchen.
         * findByEmailOrUsername(identifier, identifier):
         * → sucht in email-Spalte: identifier
         * → ODER in username-Spalte: identifier
         *
         * Beispiele:
         * identifier = "max@test.de" → findet User per Email ✓
         * identifier = "max_muster" → findet User per Username ✓
         */
        User user = userRepository.findByEmailOrUsername(identifier, identifier)
                .orElseThrow(() ->
                /*
                 * SECURITY BEST PRACTICE:
                 * Benutze IMMER die gleiche Fehlermeldung, egal ob:
                 * - User nicht gefunden
                 * - Passwort falsch
                 *
                 * Warum? "User nicht gefunden" verrät dem Angreifer,
                 * dass er einen gültigen Username/Email ausprobieren kann
                 * (User Enumeration Attack)!
                 *
                 * "Ungültige Anmeldedaten" sagt nichts Spezifisches.
                 */
                new UsernameOrEmailNotFoundException("Your credentials are invalid"));

        // ================================================================
        // SCHRITT 2: Passwort vergleichen
        // ================================================================
        /*
         * passwordEncoder.matches(rawPassword, hashedPassword)
         *
         * Was passiert intern:
         * 1. Salt aus dem Hash extrahieren (ist im Hash-String eingebettet)
         * 2. rawPassword + Salt durch BCrypt-Algorithmus jagen
         * 3. Ergebnis mit dem gespeicherten Hash vergleichen
         *
         * Warum nicht einfach nochmal hashen und vergleichen?
         * BCrypt produziert bei gleichem Input unterschiedliche Outputs (wegen
         * zufälligem Salt)!
         * matches() macht den korrekten Vergleich.
         */
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new LoginException("Your credentials are invalid"); // GLEICHE Fehlermeldung wie oben!
        }

        // ================================================================
        // SCHRITT 3 & 4: JWT Token generieren und zurückgeben
        // ================================================================
        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token, user.getRole().name());
    }
}
