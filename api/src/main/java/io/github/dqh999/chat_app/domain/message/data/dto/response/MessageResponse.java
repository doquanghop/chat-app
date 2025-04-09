package io.github.dqh999.chat_app.domain.message.data.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    String id;
    String senderId;
    String content;
}
