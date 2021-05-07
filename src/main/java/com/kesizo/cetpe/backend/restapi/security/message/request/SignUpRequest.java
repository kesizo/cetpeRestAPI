package com.kesizo.cetpe.backend.restapi.security.message.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * 17) Creating the Payload Message which is a POJO containing
 *     - name,
 *     - username
 *     - email
 *     - role
 *     - password
 */
public class SignUpRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    @NotBlank
    @Size(min = 3, max = 50)
    private String lastName;

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(max = 60)
    @Email
    private String email;

    private Set<String> role;

    @NotBlank
    @Size(min = 8, max = 40)
    private String password;

    public SignUpRequest(){}

    public SignUpRequest(@NotBlank @Size(min = 3, max = 50) String name,
                         @NotBlank @Size(min = 3, max = 50) String lastName,
                         @NotBlank @Size(min = 3, max = 50) String username,
                         @NotBlank @Size(max = 60) @Email String email,
                         Set<String> role,
                         @NotBlank @Size(min = 6, max = 40) String password) {
        this.name = name;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRole() {
        return this.role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}