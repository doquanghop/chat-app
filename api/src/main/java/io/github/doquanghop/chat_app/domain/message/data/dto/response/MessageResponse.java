package io.github.doquanghop.chat_app.domain.message.data.dto.response;

import io.github.doquanghop.chat_app.domain.user.data.model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    String id;
    User sender;
    String content;
    LocalDateTime createdAt;
}
