package io.github.dqh999.chat_app.domain.message.data.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class MessageResponse {
    String id;
    String senderId;
    String content;
}
