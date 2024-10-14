package com.enigma.tokonyadia_api.constant;

import lombok.Getter;

@Getter
public enum UserRole {

    ROLE_ADMIN("Admin"),
    ROLE_CUSTOMER("Customer");

    private final String description;


    UserRole(String description) {
        this.description = description;
    }

    public static UserRole findByName(String role) {
        for (UserRole userRole : values()) {
            if (userRole.description.equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        return null;
    }
}
