package com.kesizo.cetpe.backend.restapi.security;

import com.kesizo.cetpe.backend.restapi.security.jwt.JwtAuthEntryPoint;
import com.kesizo.cetpe.backend.restapi.security.jwt.JwtAuthTokenFilter;
import com.kesizo.cetpe.backend.restapi.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity              // 1) @EnableWebSecurity is used to enable web security in a project.
@EnableGlobalMethodSecurity(    // 2) @EnableGlobalMethodSecurity(prePostEnabled = true) is used to enable Spring Security global method security.
        prePostEnabled = true)

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsService; // 3) Service layer that we need to create for dealing with users and roles

    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler; // 4) JwtAuthEntryPoint Service in charge of handling Error exceptions when having unauthorized requests

    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter() { // 5) Service in charge of processing and validating requests with JWT authentication data
        return new JwtAuthTokenFilter();
    }

    // 6) This method creates a bean with the mechanism selected for encoding the passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();         //PasswordEncoder uses the BCrypt strong hashing funct
    }

    // 7) This method configures the authentication mechanism. In this case it will check the user details
    //    using the userDetailsService and the encryption will be done with BCrypt using the bean created on the step 6

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)     // Compare user details with the ones retrieved by userDetailsService
                .passwordEncoder(passwordEncoder());        // PasswordEncoder uses the BCrypt strong hashing funct
    }

    // 8) This method creates an authentication manager according to the configuration provided in the previous configure
    // method
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // 9) This method configures the Security system.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().                          // since we use jwt, csrf is not necessary
                authorizeRequests()
                .antMatchers("/api/auth/**").permitAll() //These urls can be accessed without auth information
                .anyRequest().authenticated()                        // All other request urls need to be validated with auth information
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler) // It uses the unauthorizedHandler object as entry point
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);  // Authentication session is without state

        // After configuring we assign the filter JWT to process the request auth before dispatching
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    }
}