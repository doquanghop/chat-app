package com.github.doquanghop.chat_app.domain.message.data.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class SendMessageRequest {
    String content;
}
