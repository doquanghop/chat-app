package io.github.dqh999.chat_app.infrastructure.utils;


import java.util.UUID;

public class DeviceUtils {

    public static String generateDeviceId(String ip, String userAgent, String browser, String os, String deviceType) {
        String raw = ip + userAgent + browser + os + deviceType;
        return UUID.nameUUIDFromBytes(raw.getBytes()).toString();
    }

}