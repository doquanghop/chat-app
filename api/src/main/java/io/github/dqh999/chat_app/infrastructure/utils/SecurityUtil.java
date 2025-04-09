package io.github.dqh999.chat_app.infrastructure.utils;

import io.github.dqh999.chat_app.infrastructure.model.AppException;
import io.github.dqh999.chat_app.infrastructure.model.UserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static UserDetail getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetail) {
            return (UserDetail) authentication.getPrincipal();
        }
        throw new AppException(ResourceException.ACCESS_DENIED);
    }

    public static String getCurrentUserId() {
        UserDetail user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
}
