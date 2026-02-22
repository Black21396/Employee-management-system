package net.fadi.ems.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.fadi.ems.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmailOrUsername(String email, String username);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);
}
