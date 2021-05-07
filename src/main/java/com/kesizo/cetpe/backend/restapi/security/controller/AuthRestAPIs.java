package com.kesizo.cetpe.backend.restapi.security.controller;

import com.kesizo.cetpe.backend.restapi.app.model.LearningStudent;
import com.kesizo.cetpe.backend.restapi.email.model.EmailBody;
import com.kesizo.cetpe.backend.restapi.email.service.EmailService;
import com.kesizo.cetpe.backend.restapi.security.jwt.JwtProvider;
import com.kesizo.cetpe.backend.restapi.security.message.request.ForgotPasswordRequest;
import com.kesizo.cetpe.backend.restapi.security.message.request.LoginRequest;
import com.kesizo.cetpe.backend.restapi.security.message.request.ResetPasswordRequest;
import com.kesizo.cetpe.backend.restapi.security.message.request.SignUpRequest;
import com.kesizo.cetpe.backend.restapi.security.message.response.JwtResponse;
import com.kesizo.cetpe.backend.restapi.security.model.Role;
import com.kesizo.cetpe.backend.restapi.security.model.RoleName;
import com.kesizo.cetpe.backend.restapi.security.model.User;
import com.kesizo.cetpe.backend.restapi.security.repository.RoleRepository;
import com.kesizo.cetpe.backend.restapi.security.service.UserService;
import com.kesizo.cetpe.backend.restapi.util.Constants;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


/**
 * 19) AuthRestAPIs.java defines at least 2 APIs:
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
    UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    @Value("${cepte.app.frontend.domain}")
    String frontEndAppDomain;


    @PostMapping("/signin") // it prefixes /api/auth because the class is annotated with @RequestMapping("/api/auth")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

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

    @GetMapping("/active/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> activeUserList(){
        return  userService.getActiveUserList();
    }


    //http://localhost:8083/api/auth/activate?code=mikydoc_sev@hotmail.comjdxyiBfpzNAzh96C46Dz86rn9PibSFATginmp71VfVjIrDIf6mM9Py4yRAaSPr9L
    @GetMapping("activate")
    public ResponseEntity<String> activateUser(@RequestParam(required = true) String email,
                                               @RequestParam(required = true) String code) {

        if (email==null) {
            return new ResponseEntity<>("Operation Failed: Email to activate the user is not provided!",
                    HttpStatus.BAD_REQUEST);
        }

        User userToActivate = userService.getUserByEmail(email).orElse(null);
        if (userToActivate==null) {
            return new ResponseEntity<>("Operation Failed: Username (email) to activate DOES NOT exist!",
                    HttpStatus.BAD_REQUEST);
        } else {
            if(userToActivate.getActivationCode().equals(code)
                    && userToActivate.getActivationCodeRequestTimeStamp()
                                     .plusHours(Constants.ACTIVATION_CODE_EXPIRATION_PERIOD_HOURS)
                                     .isAfter(LocalDateTime.now())) {

                if (userService.activate(userToActivate.getActivationCode(), email)) {
                    return ResponseEntity.ok().body("Success -> User account has been activated! Go to CEPTE login page to get started.");
                }
                else {
                    return new ResponseEntity<>("Operation Failed: Error activating user",
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            else {
                return new ResponseEntity<>("Operation Failed: Activation code IS NOT valid or expired!",
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    @PostMapping("/signup") // it prefixes /api/auth because the class is annotated with @RequestMapping("/api/auth")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        if(userService.checkExistsUserByEmail(signUpRequest.getEmail())) {

            // If the user exists and the user is active... 
            User userRegister = userService.getUserByEmail(signUpRequest.getEmail()).orElse(null);
            
            if (userRegister != null && userRegister.isActive()) { 
                return new ResponseEntity<String>("Operation Failed: Email is already in use!",
                                HttpStatus.BAD_REQUEST);  
                }
                else if (userRegister != null && !userRegister.isActive()) {

                    // Resend a new activation key with a new period
                    userRegister.setPassword(encoder.encode(signUpRequest.getPassword()));
                    userRegister.setActivationCode(RandomStringUtils.randomAlphanumeric(128));
                    userRegister.setActivationCodeRequestTimeStamp(LocalDateTime.now());

                    if (userService.saveUser(userRegister)!=null && sendActivationEmail(userRegister)) {
                        return ResponseEntity.ok().body("Activation email sent!");
                    }
                    else {
                       return new ResponseEntity<>("Operation Failed: Error processing and/or sending activation code user",
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
            }

        // Creating user's account
        User user = new User(signUpRequest.getName(), signUpRequest.getLastName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        if (strRoles==null) {
            return new ResponseEntity<String>("Operation Failed: No role is related to the sign up request!",
                    HttpStatus.BAD_REQUEST);
        }
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            switch(role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Operation Failed: User Role not find."));
                    roles.add(adminRole);

                    break;
                case "pm":
                    Role pmRole = roleRepository.findByName(RoleName.ROLE_PM)
                            .orElseThrow(() -> new RuntimeException("Operation Failed: User Role not find."));
                    roles.add(pmRole);
                    
                    break;
                default:
                    Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Operation Failed: User Role not find."));
                    roles.add(userRole);
            }
        });

        user.setRoles(roles);


        if (userService.saveUser(user)!=null && sendActivationEmail(user)) {
            return ResponseEntity.ok().body("User registered successfully! Activation email sent!");
        }
        else {
            return new ResponseEntity<>("Operation Failed: Error processing and/or sending activation code user",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/forgot-password") // it prefixes /api/auth because the class is annotated with @RequestMapping("/api/auth")
    public ResponseEntity<String> forgotPasswordRequest(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {

        if(userService.checkExistsUserByEmail(forgotPasswordRequest.getEmail())) {


            AtomicBoolean isForgotPasswordRequestSuccess = new AtomicBoolean(false);

            userService.getUserByEmail(forgotPasswordRequest.getEmail()).ifPresent(userToReset -> {
                userToReset.setResetPasswordToken(RandomStringUtils.randomAlphanumeric(128));
                userToReset.setResetPasswordTokenRequestTimeStamp(LocalDateTime.now());
                isForgotPasswordRequestSuccess.set(userService.saveUser(userToReset)!=null && sendResetPasswordEmail(userToReset));
            });

            if (isForgotPasswordRequestSuccess.get()) {
                return ResponseEntity.ok().body("Reset Password email sent!");
            }
            else {
                return new ResponseEntity<>("Operation Failed: Error processing and/or sending reset email password code",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else {
            return new ResponseEntity<>("Operation failed: Email provided does not exist!",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("reset-password") // it prefixes /api/auth because the class is annotated with @RequestMapping("/api/auth")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {

        if (resetPasswordRequest==null || resetPasswordRequest.getPasswordResetCode()==null || resetPasswordRequest.getPassword()==null || resetPasswordRequest.getEmail() == null) {
            return new ResponseEntity<>("Operation Failed: Information to reset password is not provided!",
                    HttpStatus.BAD_REQUEST);
        }

        AtomicBoolean isResetPasswordRequestSuccess = new AtomicBoolean(false);
        userService.getUserByEmail(resetPasswordRequest.getEmail()).ifPresent(userToResetPassword -> {
           if (userToResetPassword.getResetPasswordToken()!=null
                   && userToResetPassword.getResetPasswordTokenRequestTimeStamp() !=null
                   && userToResetPassword.getResetPasswordToken().equals(resetPasswordRequest.getPasswordResetCode())
                   && userToResetPassword.getResetPasswordTokenRequestTimeStamp().plusHours(Constants.RESET_PASSWORD_EXPIRATION_PERIOD_HOURS).isAfter(LocalDateTime.now()))
           {
               userToResetPassword.setPassword(encoder.encode(resetPasswordRequest.getPassword()));
               userToResetPassword.setResetPasswordToken(null);
               userToResetPassword.setResetPasswordTokenRequestTimeStamp(null);

               isResetPasswordRequestSuccess.set(userService.saveUser(userToResetPassword)!=null);
           }
        });

        if (!isResetPasswordRequestSuccess.get()) {
            return new ResponseEntity<>("Operation Failed: Username (email) to reset password DOES NOT exist or the password period has expired",
                    HttpStatus.BAD_REQUEST);
        } else {
                  return ResponseEntity.ok().body("Success -> User account has updated the password successfully. Go to CEPTE login page to get started.");
        }
    }

    private boolean sendActivationEmail(User user) {
        //Sending email to the user:
        EmailBody body = new EmailBody();
        body.setEmail(user.getEmail());
        body.setSubject(Constants.EMAIL_ACTIVATION_SUBJECT);

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                    .build()
                                                    .toUriString();

        String webLink = baseUrl+"/api/auth/activate?email="+ user.getEmail()+"&code="+ user.getActivationCode();

        body.setContent(Constants.EMAIL_ACTIVATION_CONTENT_START
                 + "<a href="+webLink +">Confirmation</a>"
                 +"<br/><br/>Click on the link or copy the following url to your browser ( "+ webLink + " )<br/>"
        +Constants.EMAIL_ACTIVATION_CONTENT_END);
        return emailService.sendEmail(body);

    }

    private boolean sendResetPasswordEmail(User user) {
        //Sending email to the user:
        EmailBody body = new EmailBody();
        body.setEmail(user.getEmail());
        body.setSubject(Constants.EMAIL_RESET_PASSWORD_SUBJECT);

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .build()
                .toUriString();

        String webLink = frontEndAppDomain+"reset-password?email="+ user.getEmail()+
                        "&resetcode="+user.getResetPasswordToken();

        body.setContent(Constants.EMAIL_FORGOT_PASSWORD_CONTENT_START
                + "<p><a href=" + webLink + ">Change my password</a></p>"
                + Constants.EMAIL_FORGOT_PASSWORD_CONTENT_END);

        return emailService.sendEmail(body);

    }


}

