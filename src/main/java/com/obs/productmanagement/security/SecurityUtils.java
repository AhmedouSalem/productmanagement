package com.obs.productmanagement.security;

import com.obs.productmanagement.dto.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static UserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        return (UserPrincipal) auth.getPrincipal();
    }

    public static String getCurrentUserEmail() {
        UserPrincipal p = getCurrentUser();
        return (p != null) ? p.email() : null;
    }

    public static Long getCurrentUserId() {
        UserPrincipal p = getCurrentUser();
        return (p != null) ? p.id() : null;
    }

    public static String getCurrentUserName() {
        UserPrincipal p = getCurrentUser();
        return (p != null) ? p.name() : null;
    }
}

