package com.kesizo.cetpe.backend.restapi.security.service;

import com.kesizo.cetpe.backend.restapi.security.model.Role;
import com.kesizo.cetpe.backend.restapi.security.model.User;

import java.util.Optional;
import java.util.Set;

public interface UserService {

    Set<Role> getUserRolesByUsername(String username);

    Set<Role> getUserRolesByEmail(String email);

    boolean checkExistsUserByUsername(String username);

    boolean checkExistsUserByEmail(String email);

    User saveUser(User user);

    Optional<User> getUserByEmail(String email);

    boolean activate(String activationCode, String email);

}
