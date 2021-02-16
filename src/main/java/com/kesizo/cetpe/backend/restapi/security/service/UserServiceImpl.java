package com.kesizo.cetpe.backend.restapi.security.service;

import com.kesizo.cetpe.backend.restapi.security.model.Role;
import com.kesizo.cetpe.backend.restapi.security.model.User;
import com.kesizo.cetpe.backend.restapi.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service("UserService")
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository _userRepository;

    @Override
    public Set<Role> getUserRolesByUsername(String username) {
        User passedUser = _userRepository.findByUsername(username).orElse(null);
        if (passedUser==null){
            logger.warn("Requested user {} does not exist",username);
            return new HashSet<Role>();
        }
        else{
            logger.debug("Returning roles for user {}",username);
           return passedUser.getRoles();
        }
    }

    @Override
    public Set<Role> getUserRolesByEmail(String email) {
        User passedUser = _userRepository.findByEmail(email).orElse(null);
        if (passedUser==null){
            logger.warn("Requested user with email {} does not exist",email);
            return new HashSet<Role>();
        }
        else{
            logger.debug("Returning roles for user with email {}",email);
            return passedUser.getRoles();
        }
    }

    @Override
    public boolean checkExistsUserByUsername(String username) {
        return  _userRepository.existsByUsername(username);
    }

    @Override
    public boolean checkExistsUserByEmail(String email) {
        return _userRepository.existsByUsername(email);
    }

    @Override
    public User saveUser(User user) {
        return _userRepository.save(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return _userRepository.findByEmail(email);
    }

    @Override
    public boolean activate(String activationCode, String email) {
        User userToActivate = _userRepository.findByActivationCode(activationCode).orElse(null);
        if (userToActivate!=null) {
            userToActivate.setActive(true);
            _userRepository.save(userToActivate);
            logger.info("User with email {} has been activated",email);
            return true;
        }
        else {
            logger.info("User with email {} CANNOT be activated",email);
            return false;
        }
    }

    @Override
    public User getUserByResetPasswordToken(String resetPasswordToken) {
        return _userRepository.findByResetPasswordToken(resetPasswordToken).orElse(null);
    }

    @Override
    public boolean updateUserResetPasswordToken(String token, String email) {
        User user = _userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setResetPasswordToken(token);
            return _userRepository.save(user)!=null;
        } else {
            logger.warn("Could not find any user with the email " + email);
            return false;
        }
    }

    @Override
    public boolean updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetPasswordToken(null);
        return _userRepository.save(user)!=null;
    }


}
