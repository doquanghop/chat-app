package io.github.dqh999.chat_app.domain.conversation.data.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationEvent<T> {
    String requestId;
    Type type;
    T payload;

    public enum Type {
        MESSAGE,
        MEMBER_JOIN,
        MEMBER_LEAVE,
        MEMBER_UPDATE,
        CONVERSATION_UPDATE
    }
}
