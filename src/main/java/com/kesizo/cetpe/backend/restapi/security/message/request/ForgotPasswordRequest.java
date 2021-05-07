package com.kesizo.cetpe.backend.restapi.security.message.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class ForgotPasswordRequest {

    @NotBlank
    @Email
    private String email;

    public ForgotPasswordRequest(){

    }
    public ForgotPasswordRequest(@NotBlank @Email String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
