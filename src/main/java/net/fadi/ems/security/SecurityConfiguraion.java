package net.fadi.ems.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.RequiredArgsConstructor;

/**
 * Zentrale Security-Konfiguration.
 *
 * @Configuration → Markiert als Spring Konfigurationsklasse
 * @EnableWebSecurity → Aktiviert Spring Security (ersetzt die
 *                    Default-Konfiguration)
 * @EnableMethodSecurity → Aktiviert @PreAuthorize, @PostAuthorize auf
 *                       Methoden-Ebene
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguraion {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * Hauptkonfiguration der Security Filter Chain.
     *
     * Hier definieren wir:
     * 1. CSRF-Schutz (deaktiviert für REST APIs)
     * 2. CORS-Konfiguration (für Next.js Frontend)
     * 3. Session-Management (stateless für JWT)
     * 4. Endpoint-Zugriffs-Regeln
     * 5. Unser JWT-Filter einbinden
     *
     * @Bean → Spring registriert den Rückgabewert als Bean im ApplicationContext
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /*
                 * CSRF (Cross-Site Request Forgery) deaktivieren.
                 *
                 * Warum? CSRF-Angriffe funktionieren nur mit Sessions (Cookies).
                 * Da wir JWT (stateless) nutzen, gibt es keine Session die gestohlen werden
                 * kann.
                 * → CSRF-Schutz unnötig und würde unsere API-Calls blockieren!
                 *
                 * ACHTUNG: Wenn du jemals auf Cookie-basierte Auth wechselst, CSRF wieder
                 * aktivieren!
                 */
                .csrf(AbstractHttpConfigurer::disable)

                /*
                 * CORS (Cross-Origin Resource Sharing) konfigurieren.
                 *
                 * Warum brauchen wir CORS?
                 * Next.js Frontend läuft auf: http://localhost:3000
                 * Spring Backend läuft auf: http://localhost:8080
                 * Verschiedene Ports = verschiedene "Origins" = Browser blockiert Requests!
                 *
                 * CORS erlaubt uns, bestimmte Origins zu erlauben.
                 */
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                /*
                 * Session-Management auf STATELESS setzen.
                 *
                 * STATELESS bedeutet:
                 * - Spring Security erstellt KEINE Server-Side Sessions
                 * - Kein JSESSIONID-Cookie wird gesetzt
                 * - Jeder Request muss eigenständig authentifiziert werden (via JWT)
                 * - Server-RAM wird nicht für Sessions verbraucht
                 * - Perfekt für horizontale Skalierung (mehrere Server-Instanzen)
                 */
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                /*
                 * Endpoint-Zugriffsregeln.
                 *
                 * Reihenfolge ist WICHTIG! Spezifischere Regeln zuerst!
                 * Spring Security prüft von oben nach unten und nimmt die ERSTE passende Regel.
                 */
                .authorizeHttpRequests(auth -> auth
                        /*
                         * /api/auth/** = alle URLs die mit /api/auth/ beginnen
                         * permitAll() = ohne Authentication zugänglich (Login, Register)
                         */
                        .requestMatchers("/api/auth/**").permitAll()

                        /*
                         * Swagger/OpenAPI Dokumentation (optional, für Entwicklung)
                         * Im Produktionsmodus sollte das entfernt oder durch IP-Whitelist gesichert
                         * werden!
                         */
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()

                        /*
                         * actutor health Endpoints should be public to check the health of the backend
                         * in container
                         * Note: I made the health of backend is important to run the frontend
                         * container. see docker-compose.yml
                         */
                        .requestMatchers("/actuator/health/**").permitAll()

                        /*
                         * Admin-Endpoints: Nur für ADMIN Rolle zugänglich.
                         * hasRole("ADMIN") = prüft ob "ROLE_ADMIN" in den authorities ist
                         */
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        /*
                         * Alle anderen Requests brauchen gültige Authentication.
                         * D.h. ein gültiger JWT Token muss im Authorization-Header sein.
                         */
                        .anyRequest().authenticated())

                /*
                 * Unseren JWT Filter VOR dem Standard UsernamePasswordAuthenticationFilter
                 * einbinden.
                 *
                 * Warum vor UsernamePasswordAuthenticationFilter?
                 * Dieser Filter versucht Username/Passwort aus dem Request-Body zu lesen.
                 * Wir wollen aber zuerst den JWT Token prüfen.
                 * Wenn JWT gültig ist, überspringt Spring den Standard-Filter.
                 */
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCryptPasswordEncoder als Bean registrieren.
     *
     * BCrypt ist der Standard für Passwort-Hashing:
     * - Automatisches Salting (kein Rainbow-Table Angriff möglich)
     * - Konfigurierbarer Strength (Standard: 10 Runden)
     * - Bewusst langsam: macht Brute-Force Angriffe extrem teuer
     *
     * Warum als @Bean?
     * → Kann überall injiziert werden (@Autowired / Constructor Injection)
     * → Nur eine Instanz im gesamten ApplicationContext (Singleton)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        /*
         * Strength 10 = 2^10 = 1024 Iterationen (Standard, gut ausgewogen)
         * Strength 12 = 4096 Iterationen (sicherer, aber langsamer)
         *
         * Empfehlung: 10-12 für normale Apps
         */
        return new BCryptPasswordEncoder(10);
    }

    /**
     * AuthenticationProvider konfigurieren.
     *
     * DaoAuthenticationProvider ist der Standard-Provider:
     * - DAO = Data Access Object → liest User aus DB via UserDetailsService
     * - Verwendet unseren PasswordEncoder für Passwort-Vergleich
     *
     * Ablauf bei Authentication:
     * 1. loadUserByUsername(username) → User aus DB laden
     * 2. passwordEncoder.matches(rawPassword, hashedPassword) → Passwort prüfen
     * 3. Wenn korrekt → Authentication-Objekt erstellen
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }
}
