package io.github.dqh999.chat_app.domain.conversation.data.dto.response;

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
    String type;
    long unreadCount;
}
