package com.kesizo.cetpe.backend.restapi.repository;

import com.kesizo.cetpe.backend.restapi.security.model.Role;
import com.kesizo.cetpe.backend.restapi.security.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository("roleRepository")
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
