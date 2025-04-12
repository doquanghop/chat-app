package io.github.doquanghop.chat_app.domain.conversation.data.dto;

import io.github.doquanghop.chat_app.infrastructure.model.HasRequestId;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationEvent<T> implements HasRequestId {
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

    @Override
    public String getRequestId() {
        return requestId;
    }
}
