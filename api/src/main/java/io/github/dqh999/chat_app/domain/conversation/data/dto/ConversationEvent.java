package io.github.dqh999.chat_app.domain.conversation.data.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
public class ConversationEvent<T> {
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
