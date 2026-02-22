package net.fadi.ems.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.fadi.ems.exception.UsernameOrEmailNotFoundException;
import net.fadi.ems.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Lädt einen User aus der DB anhand des Usernamens (oder der Email).
     *
     * WICHTIG: Dieser "username"-Parameter kann bei uns auch eine Email sein!
     * Spring Security kennt das Konzept von Email nicht – es arbeitet immer mit
     * "username".
     * 
     * Deshalb suchen wir in BEIDEN Feldern (email UND username):
     * findByEmailOrUsername("max@test.de", "max@test.de")
     * → Findet User entweder per Email oder Username
     *
     * @param identifier Username oder Email des Users
     * @return UserDetails (unsere User-Klasse implementiert das)
     * @throws UsernameNotFoundException wenn User nicht in DB gefunden
     */

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        return userRepository.findByEmailOrUsername(identifier, identifier)
                .orElseThrow(() ->
                /*
                 * UsernameNotFoundException wird von Spring Security erwartet.
                 * Spring Security fängt sie ab und wandelt sie in eine
                 * BadCredentialsException um (für Security-Gründe).
                 *
                 * Logge diesen Fehler NICHT mit dem echten identifier!
                 * Logging könnte als Audit-Trail für User-Enumeration missbraucht werden.
                 */
                new UsernameOrEmailNotFoundException("Username or email does not exist"));
    }

}
