package com.kesizo.cetpe.backend.restapi.security.repository;

import com.kesizo.cetpe.backend.restapi.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndActive(String username, boolean active);

    Boolean existsByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByActivationCode(String activationCode);
}
