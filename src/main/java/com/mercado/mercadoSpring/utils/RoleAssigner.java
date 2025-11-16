package com.mercado.mercadoSpring.utils;

import com.mercado.mercadoSpring.constants.user.UserRole;

import java.util.Map;

public class RoleAssigner {

    // Map of Email to Roles
    private static  final Map<String, UserRole> emailRoleMap = Map.of(
            "@admin.co", UserRole.ADMIN,
            "@seller", UserRole.SELLER,
            "@dev", UserRole.DEVELOPER,
            "@cloud", UserRole.DEVOPS
    );

    /**
     * Assigns a role based on the user's email.
     * Defaults to CUSTOMER if no pattern matches.
     */
    public static UserRole assignRole(String email) {
        String lowerCaseEmail = email.toLowerCase().trim();
        for (Map.Entry<String, UserRole> entry : emailRoleMap.entrySet()) {
            if (lowerCaseEmail.contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        return UserRole.CUSTOMER; // Default role
    }
}
