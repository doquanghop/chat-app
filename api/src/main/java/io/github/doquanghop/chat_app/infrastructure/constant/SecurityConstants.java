package io.github.doquanghop.chat_app.infrastructure.constant;

import java.util.Arrays;
import java.util.List;

/**
 * Contains security-related constants used across the application.
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // Prevent instantiation
    }

    /**
     * List of endpoints that do not require JWT authentication.
     */
    public static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/v1/account/login",
            "/api/v1/account/register",
            "/api/v1/account/refreshToken",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/ws/**"
    );

    public static final String APP_SESSION_ID_KEY = "appSessionId";

}