package com.kesizo.cetpe.backend.restapi.security.jwt;


import com.kesizo.cetpe.backend.restapi.security.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * In the security/jwt package we define the JWT Authentication Classes
 *
 * 12) This class, JwtAuthTokenFilter extends OncePerRequestFilter (org.springframework.web.filter.OncePerRequestFilter)
 *     - Executes once per request.
 *     - This is a filter base class that is used to guarantee a single execution per request dispatch.
 *     - It provides a doFilterInternal method with HttpServletRequest and HttpServletResponse arguments.
 *
 */
public class JwtAuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider tokenProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthTokenFilter.class);

    // 13) This JwtAuthTokenFilter class has to override the doFilterInternal method by doing:
    //
    //      a) get JWT token from header (auxiliary method below)
    //      b) validate JWT (using the tokenProvider field from JwtProvider class)
    //      c) parse username from validated JWT (using the tokenProvider field from JwtProvider class)
    //      d) load data from users table (using the userDetailsServiceImpl)
    //      e) then build an authentication object
    //      f) set the authentication object to Security Context
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {

            // a) get JWT token from header (auxiliary method below)
            String jwt = getJwt(request);

            // b) validate JWT (using the tokenProvider field from JwtProvider class)
            if (jwt!=null && tokenProvider.validateJwtToken(jwt)) {

            // c) parse username from validated JWT (using the tokenProvider field from JwtProvider class)
                String username = tokenProvider.getUserNameFromJwtToken(jwt);

            // d) load data from users table (using the userDetailsServiceImpl)
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

             // e) then build an authentication object
                UsernamePasswordAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

             // f) set the authentication object to Security Context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Can NOT set user authentication -> Message: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ","");
        }

        return null;
    }
}
