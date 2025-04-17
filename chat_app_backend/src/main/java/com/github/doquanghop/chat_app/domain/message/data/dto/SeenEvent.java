package com.github.doquanghop.chat_app.domain.message.data.dto;

import com.github.doquanghop.chat_app.infrastructure.model.HasRequestId;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeenEvent implements HasRequestId {
    String requestId;
    String conversationId;
    String userId;
    String username;
    String messageId;

    @Override
    public String getRequestId() {
        return requestId;
    }
}