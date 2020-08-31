package com.kesizo.cetpe.backend.restapi.security.jwt;


import com.kesizo.cetpe.backend.restapi.security.service.UserPrinciple;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 15) This class represent the service in charge of:
 *
 *  - a) Retrieves the jwt secret seed
 *  - b) the jwt life time (using properties provided as application properties)
 *  - c) Generates the String representing the jwt
 *
 *  - It also provides a method for:
 *      d) extracts the username from the jwt token
 *      e) Validate the Jwt tokens
 *
 */
@Component
public class JwtProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);


    @Value("${cepte.app.jwtSecret}")     // a) key used to generate the jwt, it needs to be passed as property
    private String jwtSecret;

    @Value("${cepte.app.jwtExpiration}") // b) expiration for the tokens
    private int jwtExpiration;

    // c) method for creating the token
    public String generateJwtToken(Authentication authentication) {

        UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // d) extracts the username from the jwt token
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    // e) Validates the jwt token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature -> Message: {} ", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token -> Message: {}", e);
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token -> Message: {}", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token -> Message: {}", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty -> Message: {}", e);
        }

        return false;
    }
}