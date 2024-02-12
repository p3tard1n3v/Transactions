package com.merchant.transactions.security;

import com.merchant.transactions.model.UserEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    public static String getSessionUser() {
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return null;
    }

    public static boolean isAdminUser() {
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getAuthorities().stream().anyMatch(role ->
                    role.getAuthority().equals(UserEntity.class.getSimpleName()));
        }
        return false;
    }
}