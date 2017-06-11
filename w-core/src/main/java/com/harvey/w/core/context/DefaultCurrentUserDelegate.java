package com.harvey.w.core.context;

import com.harvey.w.core.model.UserBaseModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class DefaultCurrentUserDelegate implements CurrentUserDelegate {

    @SuppressWarnings("unchecked")
    @Override
    public <T extends UserBaseModel> T getCurrentUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object user = authentication.getPrincipal();
        if (user instanceof UserBaseModel) {
            return (T) authentication.getPrincipal();
        }
        return null;
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
