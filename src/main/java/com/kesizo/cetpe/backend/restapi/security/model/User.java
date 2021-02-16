package com.kesizo.cetpe.backend.restapi.security.model;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "username"
        }),
        @UniqueConstraint(columnNames = {
                "email"
        })
})
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min=3, max = 50)
    private String name;

    @NotBlank
    @Size(min=3, max = 50)
    private String username;

    @NaturalId
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min=6, max = 100)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Size(max = 128)
    private String activationCode;

    private LocalDateTime activationCodeRequestTimeStamp;

    private boolean active;

    @Size(max = 128)
    private String resetPasswordToken;

    public User() {}

    public User(String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.activationCode = RandomStringUtils.randomAlphanumeric(128);
        this.activationCodeRequestTimeStamp = LocalDateTime.now();
        this.active=false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public LocalDateTime getActivationCodeRequestTimeStamp() {
        return activationCodeRequestTimeStamp;
    }

    public void setActivationCodeRequestTimeStamp(LocalDateTime activationCodeRequestTimeStamp) {
        this.activationCodeRequestTimeStamp = activationCodeRequestTimeStamp;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return active == user.active && id.equals(user.id) && name.equals(user.name) && username.equals(user.username) && email.equals(user.email) && password.equals(user.password) && roles.equals(user.roles) && activationCode.equals(user.activationCode) && activationCodeRequestTimeStamp.equals(user.activationCodeRequestTimeStamp) && resetPasswordToken.equals(user.resetPasswordToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, username, email, password, roles, activationCode, activationCodeRequestTimeStamp, active, resetPasswordToken);
    }

}