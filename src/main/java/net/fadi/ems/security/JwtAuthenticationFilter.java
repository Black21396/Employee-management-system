package net.fadi.ems.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.fadi.ems.service.JwtService;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JwtService für Token-Validierung und -Parsing.
     * UserDetailsService zum Laden des Users aus der DB.
     *
     * final + @RequiredArgsConstructor = Constructor Injection (empfohlen
     * gegenüber @Autowired)
     * Vorteile: Immutability, bessere Testbarkeit, kein null möglich
     */
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Diese Methode wird für jeden HTTP-Request aufgerufen.
     *
     * @param request     Der eingehende HTTP-Request (enthält Header, Body, etc.)
     * @param response    Die ausgehende HTTP-Response
     * @param filterChain Die Kette der nächsten Filter
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // ================================================================
        // SCHRITT 1: Authorization Header lesen
        // ================================================================
        /*
         * Standard HTTP Authorization Header Format:
         * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
         *
         * request.getHeader("Authorization") → gibt den Header-Wert zurück
         * Wenn kein Token gesendet wurde → null
         */
        final String authHeader = request.getHeader("Authorization");

        /*
         * Kein Authorization Header ODER fängt nicht mit "Bearer " an?
         * → Kein JWT Token im Request
         * → filterChain.doFilter() → weiter zum nächsten Filter
         * → Spring Security wird den Request dann je nach Konfiguration
         * entweder erlauben (public endpoints) oder ablehnen (geschützte endpoints)
         *
         * WICHTIG: return nach doFilter() – kein Code danach ausführen!
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ================================================================
        // SCHRITT 2: JWT Token extrahieren
        // ================================================================
        /*
         * authHeader = "Bearer eyJhbGci..."
         * substring(7) → überspringt "Bearer " (7 Zeichen inkl. Leerzeichen)
         * jwt = "eyJhbGci..."
         */
        final String jwt = authHeader.substring(7);

        // Username aus dem Token extrahieren
        final String username;
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            /*
             * Wenn Token ungültig/manipuliert/abgelaufen → Exception
             * Wir fangen sie ab, loggen sie (optional) und weiter im Filter.
             * Spring Security wird den Request dann automatisch ablehnen.
             */
            filterChain.doFilter(request, response);
            return;
        }

        // ================================================================
        // SCHRITT 3: User laden und Token validieren
        // ================================================================
        /*
         * username != null → Token hatte einen Subject-Claim
         * getAuthentication() == null → User ist noch nicht authentifiziert
         * (verhindert doppelte Verarbeitung wenn schon authentifiziert)
         */
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            /*
             * User aus DB laden.
             * loadUserByUsername() sucht in unserer CustomUserDetailsService.
             * Die Methode gibt unsere User-Klasse zurück (die UserDetails implementiert).
             *
             * Ja, das ist eine DB-Abfrage bei jedem Request!
             * Trade-off: Sicherheit vs. Performance
             * Vorteil: Wenn User gesperrt wird, greift das sofort
             * Optimierung: Redis-Cache vor DB-Abfrage (Profi-Level)
             */
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            /*
             * Token validieren:
             * 1. Username im Token == Username in DB?
             * 2. Token nicht abgelaufen?
             */
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // ================================================================
                // SCHRITT 4: User im SecurityContext setzen
                // ================================================================
                /*
                 * UsernamePasswordAuthenticationToken ist die Standard-Implementierung
                 * von Authentication in Spring Security.
                 *
                 * Parameter:
                 * 1. principal (= userDetails) → der authentifizierte User
                 * 2. credentials (= null) → Passwort brauchen wir nicht mehr (bereits
                 * verifiziert)
                 * 3. authorities → Rollen/Rechte des Users (aus UserDetails)
                 *
                 * Warum Rollen mitgeben? Spring Security prüft später .hasRole("ADMIN")
                 * anhand dieser authorities!
                 */
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                /*
                 * Request-Details hinzufügen (IP-Adresse, Session-ID etc.)
                 * Optional aber Best Practice für Logging und Audit.
                 */
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                /*
                 * User als authentifiziert im SecurityContext setzen.
                 * Ab jetzt "weiß" Spring Security, wer dieser Request ist.
                 * Alle nachfolgenden Security-Checks nutzen diesen Context.
                 */
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // ================================================================
        // SCHRITT 5: Zum nächsten Filter weiterleiten
        // ================================================================
        /*
         * IMMER aufrufen! Sonst bleibt der Request hängen.
         * Der nächste Filter in der Kette prüft dann die Authorization-Regeln.
         */
        filterChain.doFilter(request, response);
    }
}