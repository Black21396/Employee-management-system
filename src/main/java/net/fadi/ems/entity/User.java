package net.fadi.ems.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fadi.ems.model.Role;

@Entity
@Table(name = "users") // "user" ist ein reserviertes Wort in PostgreSQL → "users" nutzen!
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-Increment in DB
    private Long id;

    /**
     * unique = true → DB-Constraint: Kein zweiter User mit gleicher Email möglich
     * nullable = false → Email darf nicht null sein
     */
    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Hier wird NUR das gehashte Passwort gespeichert!
     * Das Original-Passwort verlässt niemals den AuthService.
     * BCrypt-Hash sieht so aus:
     * "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
     */
    @Column(nullable = false)
    private String password;

    /**
     * @Enumerated(EnumType.STRING) → Speichert den String-Wert ("USER" oder
     * "ADMIN")
     * Nicht ORDINAL (0 oder 1) – das ist fehleranfällig bei Änderungen!
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // =====================================================================
    // UserDetails Interface Methoden – Spring Security braucht diese!
    // =====================================================================

    /**
     * Gibt die Rollen/Rechte des Users zurück.
     *
     * Spring Security erwartet "ROLE_" als Prefix!
     * Deshalb: "ROLE_" + role.name() = "ROLE_USER" oder "ROLE_ADMIN"
     *
     * Das ist wichtig für:
     * - .hasRole("ADMIN") → prüft ob "ROLE_ADMIN" vorhanden
     * - .hasAuthority("ROLE_ADMIN") → prüft exakt diesen String
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Spring Security nutzt getUsername() als eindeutigen Identifier.
     * Wir geben hier den USERNAME zurück (nicht die Email!).
     * 
     * Für Login per Email oder Username haben wir eigene Logik im AuthService.
     * Im JWT-Token speichern wir den Username (als "subject").
     */
    @Override
    public String getUsername() {
        return username; // WICHTIG: username-Feld zurückgeben, nicht email!
    }

    /**
     * Diese 4 Methoden kontrollieren ob der Account nutzbar ist.
     * Für unseren Fall geben wir immer true zurück.
     *
     * In komplexeren Anwendungen könnte man hier z.B. prüfen:
     * - isAccountNonLocked: Ist der Account nach 5 Fehlversuchen gesperrt?
     * - isEnabled: Wurde die Email-Adresse bestätigt?
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
