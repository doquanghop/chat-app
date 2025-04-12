package io.github.doquanghop.chat_app.domain.message.data.dto;

import io.github.doquanghop.chat_app.infrastructure.model.HasRequestId;

public class TypingEvent implements HasRequestId {
    private String requestId;
    private String conversationId;
    private String userId;
    private String username;
    private boolean isTyping;

    @Override
    public String getRequestId() {
        return "";
    }
}
