package io.github.doquanghop.chat_app.domain.conversation.data.dto.response;

import io.github.doquanghop.chat_app.domain.conversation.data.model.ConversationType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ConversationResponse {
    String id;
    String name;
    String avatarURL;
    ConversationType type;
    long unreadCount;
}
