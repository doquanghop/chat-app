package io.github.doquanghop.chat_app.infrastructure.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class DeviceIdUtils {

    public static String generateDeviceId(String accountId, String userAgent) {
        String combined = accountId + ":" + userAgent;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to generate deviceId", e);
        }
    }
}