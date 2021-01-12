package com.kesizo.cetpe.backend.restapi.security.service;

import com.kesizo.cetpe.backend.restapi.security.model.Role;
import com.kesizo.cetpe.backend.restapi.security.model.User;
import com.kesizo.cetpe.backend.restapi.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service("UserService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository _userRepository;

    @Override
    public Set<Role> getUserRolesByUsername(String username) {
        User passedUser = _userRepository.findByUsername(username).orElse(null);
        if (passedUser==null){
            return new HashSet<Role>();
        }
        else{
           return passedUser.getRoles();
        }
    }

    @Override
    public Set<Role> getUserRolesByEmail(String email) {
        User passedUser = _userRepository.findByEmail(email).orElse(null);
        if (passedUser==null){
            return new HashSet<Role>();
        }
        else{
            return passedUser.getRoles();
        }
    }
}
