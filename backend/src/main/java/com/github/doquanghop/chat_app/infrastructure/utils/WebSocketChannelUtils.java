package com.github.doquanghop.chat_app.infrastructure.utils;

public class WebSocketChannelUtils {
    public static final String CONVERSATION_PREFIX = "/topic/conversation.";
    public static final String USER_QUEUE_PREFIX = "/user/%s/queue/messages";
    public static final String NOTIFICATION_QUEUE_PREFIX = "/user/%s/queue/notifications";
    public static final String GLOBAL_ANNOUNCEMENT_CHANNEL = "/topic/global-announcements";

    public static String buildConversationChannel(String conversationId) {
        return CONVERSATION_PREFIX + conversationId;
    }

    public static String buildUserQueueChannel(String userId) {
        return String.format(USER_QUEUE_PREFIX, userId);
    }

    public static String buildNotificationChannel(String userId) {
        return String.format(NOTIFICATION_QUEUE_PREFIX, userId);
    }

    public static String extractConversationId(String channel) {
        return channel.replace(CONVERSATION_PREFIX, "");
    }
}
