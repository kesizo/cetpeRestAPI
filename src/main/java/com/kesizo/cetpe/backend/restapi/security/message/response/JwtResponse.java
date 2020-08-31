package com.kesizo.cetpe.backend.restapi.security.message.response;

/**
 * 18) JwtResponse a POJO returned by SpringBoot server after successful authentication, it contains 2 parts:
 *
 *      - JWT Token
 *      - Schema Type of Token
 */

public class JwtResponse {

    private String token;
    private String type = "Bearer";

    public JwtResponse(String accessToken) {
        this.token = accessToken;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }
}
