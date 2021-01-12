package com.kesizo.cetpe.backend.restapi.security.service;

import com.kesizo.cetpe.backend.restapi.security.model.Role;
import com.kesizo.cetpe.backend.restapi.security.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("RoleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository _roleRepository;

    @Override
    public List<Role> getRolesList() {
        return _roleRepository.findAll();
    }
}
