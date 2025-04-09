package io.github.dqh999.chat_app.infrastructure.util;

public class ChannelUtils {
    public static final String CONVERSATION_PREFIX = "chat.conversation.";
    public static final String CONVERSATION_PATTERN = CONVERSATION_PREFIX + "*";

    public static String extractConversationId(String channel) {
        return channel.replace(CONVERSATION_PREFIX, "");
    }

    public static String buildConversationChannel(String conversationId) {
        return CONVERSATION_PREFIX + conversationId;
    }
}