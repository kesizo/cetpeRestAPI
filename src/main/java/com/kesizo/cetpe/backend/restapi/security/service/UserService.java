package com.kesizo.cetpe.backend.restapi.security.service;

import com.kesizo.cetpe.backend.restapi.security.model.Role;

import java.util.Set;

public interface UserService {

    public Set<Role> getUserRolesByUsername(String username);

    public Set<Role> getUserRolesByEmail(String email);
}
