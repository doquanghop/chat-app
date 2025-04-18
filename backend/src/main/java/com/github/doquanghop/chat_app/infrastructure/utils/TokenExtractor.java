package com.github.doquanghop.chat_app.infrastructure.utils;


public class TokenExtractor {

    public static String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}