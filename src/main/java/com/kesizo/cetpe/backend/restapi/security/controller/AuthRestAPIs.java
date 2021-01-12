package com.kesizo.cetpe.backend.restapi.security.controller;

import com.kesizo.cetpe.backend.restapi.security.jwt.JwtProvider;
import com.kesizo.cetpe.backend.restapi.security.message.request.LoginForm;
import com.kesizo.cetpe.backend.restapi.security.message.request.SignUpForm;
import com.kesizo.cetpe.backend.restapi.security.message.response.JwtResponse;
import com.kesizo.cetpe.backend.restapi.security.model.Role;
import com.kesizo.cetpe.backend.restapi.security.model.RoleName;
import com.kesizo.cetpe.backend.restapi.security.model.User;
import com.kesizo.cetpe.backend.restapi.security.repository.RoleRepository;
import com.kesizo.cetpe.backend.restapi.security.repository.UserRepository;
import com.kesizo.cetpe.backend.restapi.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 19) AuthRestAPIs.java defines 2 APIs:
 *
 *     /api/auth/signup: sign up
 *        -> check username/email is already in use.
 *        -> create User object
 *        -> store to database
 *
 *     /api/auth/signin: sign in
 *        -> attempt to authenticate with AuthenticationManager bean.
 *        -> add authentication object to SecurityContextHolder
 *        -> Generate JWT token, then return JWT to client
 *
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthRestAPIs {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/signin") // it prefixes /api/auth because the class is annotated with @RequestMapping("/api/auth")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @GetMapping("/roles/{username}") // it prefixes /api/auth because the class is annotated with @RequestMapping("/api/auth")
    @PreAuthorize("(hasRole('USER') and authentication.name==#username) or hasRole('PM') or hasRole('ADMIN')")
    public ResponseEntity<Set<String>> getRolesByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserRolesByUsername(username)
                            .stream()
                            .map(role ->role.getName().toString())
                            .collect(Collectors.toSet()));
    }

    @PostMapping("/signup") // it prefixes /api/auth because the class is annotated with @RequestMapping("/api/auth")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<String>("Fail -> Username is already taken!",
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<String>("Fail -> Email is already in use!",
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        if (strRoles==null) {
            return new ResponseEntity<String>("Fail -> No role is related to the sign up request!",
                    HttpStatus.BAD_REQUEST);
        }
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            switch(role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                    roles.add(adminRole);

                    break;
                case "pm":
                    Role pmRole = roleRepository.findByName(RoleName.ROLE_PM)
                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                    roles.add(pmRole);

                    break;
                default:
                    Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                    roles.add(userRole);
            }
        });

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok().body("User registered successfully!");
    }
}

