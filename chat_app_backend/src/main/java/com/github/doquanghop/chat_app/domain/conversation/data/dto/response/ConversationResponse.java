package com.github.doquanghop.chat_app.domain.conversation.data.dto.response;

import com.github.doquanghop.chat_app.domain.conversation.data.model.ConversationType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ConversationResponse {
    String id;
    String name;
    String avatarURL;
    ConversationType type;
    long unreadCount;
    boolean isOnline = true;
    LocalDateTime lastSeen = LocalDateTime.now();
}
