package com.kesizo.cetpe.backend.restapi.security.model;

public enum  RoleName {
    ROLE_USER,// It has to be prefixed with ROLE_ otherwise it does not work with the level method Spring Security using @PreAuthorize
    ROLE_PM,
    ROLE_ADMIN
}
