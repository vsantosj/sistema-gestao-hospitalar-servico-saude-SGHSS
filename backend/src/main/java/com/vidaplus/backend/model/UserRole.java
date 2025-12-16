package com.vidaplus.backend.model;

public enum UserRole {
    ADMIN("admin"),
    USER("user"),
    DOCTOR("doctor"),
    PATIENT("patient");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}