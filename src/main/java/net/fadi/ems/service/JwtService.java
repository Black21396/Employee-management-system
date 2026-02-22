package net.fadi.ems.service;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    @Value("${app.jwt.secretkey}")
    private String secretKey;
    @Value("${app.jwt.expiration}")
    private long expiration;

    public String generateToken(UserDetails userDetails) {
        // Rollen aus dem UserDetails-Objekt extrahieren (z.B. "ROLE_USER")
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                /*
                 * subject → der eindeutige Identifier des Users im Token
                 * Wir nehmen den Username (nicht die Email!)
                 * Warum? UserDetails.getUsername() gibt unseren username zurück.
                 * Damit können wir den User beim nächsten Request wiederfinden.
                 */
                .subject(userDetails.getUsername())

                /*
                 * claim() → eigene Daten in den Token-Payload einfügen
                 * Hier speichern wir die Rollen, damit der JwtAuthFilter
                 * später ohne DB-Abfrage weiß, welche Rechte der User hat.
                 *
                 * Format: { "roles": ["ROLE_USER"] }
                 */
                .claim("roles", roles)

                /*
                 * issuedAt → Timestamp "wann wurde der Token erstellt?"
                 * new Date() = aktuelle Zeit
                 */
                .issuedAt(new Date())

                /*
                 * expiration → Timestamp "wann läuft der Token ab?"
                 * System.currentTimeMillis() + expiration = jetzt + 1 Tag (in ms)
                 *
                 * Nach diesem Zeitpunkt ist der Token UNGÜLTIG,
                 * auch wenn die Signatur korrekt ist!
                 */
                .expiration(new Date(System.currentTimeMillis() + expiration))

                /*
                 * signWith() → Token mit dem Secret Key signieren
                 * Algorithmus: HMAC-SHA256 (wird automatisch aus Key abgeleitet)
                 *
                 * Die Signatur verhindert Manipulation:
                 * Wenn jemand den Payload ändert, stimmt die Signatur nicht mehr!
                 */
                .signWith(getSigningKey())

                // Token bauen und als String zurückgeben
                .compact();
    }

    /**
     * Extrahiert den Username (Subject) aus dem Token.
     *
     * Wird im JwtAuthFilter aufgerufen:
     * 1. Token aus dem Authorization-Header lesen
     * 2. Username aus Token extrahieren
     * 3. User aus DB laden
     * 4. Token validieren
     */
    public String extractUsername(String token) {
        /*
         * Claims::getSubject → Method Reference
         * Equivalent zu: (claims) -> claims.getSubject()
         *
         * Claims sind die "Behauptungen" im Token-Payload:
         * subject, roles, iat, exp – das sind alles Claims
         */
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Validiert ob der Token gültig ist für den gegebenen User.
     *
     * Zwei Bedingungen müssen erfüllt sein:
     * 1. Username im Token muss mit dem geladenen User übereinstimmen
     * 2. Token darf nicht abgelaufen sein
     *
     * Warum Username vergleichen?
     * Ohne diesen Check könnte jemand einen gültigen Token von User A nehmen
     * und User B damit authentifizieren. Das verhindern wir hier.
     *
     * @param token       Der JWT Token aus dem Authorization-Header
     * @param userDetails Der aus der DB geladene User
     * @return true wenn Token gültig und zum User gehört
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String tokenUsername = extractUsername(token);
        return tokenUsername.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Prüft ob der Token abgelaufen ist.
     *
     * extractClaim(token, Claims::getExpiration) → liest das "exp" Feld aus dem
     * Token
     * .before(new Date()) → ist das Ablaufdatum vor dem aktuellen Zeitpunkt?
     *
     * @return true wenn Token ABGELAUFEN ist
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Generische Methode um beliebige Claims aus dem Token zu extrahieren.
     *
     * Warum generisch (<T>)?
     * - extractClaim(token, Claims::getSubject) → gibt String zurück
     * - extractClaim(token, Claims::getExpiration) → gibt Date zurück
     * - extractClaim(token, Claims::getIssuedAt) → gibt Date zurück
     *
     * Eine Methode für alle Typen! Das ist der Vorteil von Generics + Function<>.
     *
     * @param token          Der JWT Token
     * @param claimsResolver Eine Funktion, die aus Claims das gewünschte Feld liest
     * @param <T>            Der Rückgabetyp (wird automatisch inferiert)
     * @return Der extrahierte Wert
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        /*
         * Jwts.parser() → startet den Token-Parser
         * .verifyWith(getSigningKey()) → setzt den Key zum Überprüfen der Signatur
         * .build() → baut den Parser
         * .parseSignedClaims(token) → parsed UND validiert die Signatur
         * → wirft Exception wenn Signatur ungültig oder Token manipuliert!
         * .getPayload() → gibt die Claims zurück
         */
        final Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // Gewünschten Claim aus dem Payload extrahieren
        return claimsResolver.apply(claims);
    }

    /**
     * Erstellt den SecretKey aus dem Base64-kodierten String.
     *
     * Decoders.BASE64.decode(secretKey) → dekodiert den Base64-String zu bytes
     * Keys.hmacShaKeyFor(bytes) → erstellt einen HMAC-SHA Key
     *
     * Warum Base64?
     * Weil der Secret Key Binärdaten enthält, die nicht direkt als String
     * gespeichert
     * werden können. Base64 ist eine sichere Textrepräsentation von Binärdaten.
     *
     * @return SecretKey für JWT Signatur/Verifikation
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
