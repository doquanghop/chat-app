package io.github.dqh999.chat_app.domain.message.data.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class SearchMessageRequest {
    String conversationId;
    String keyword;
    int page;
    int pageSize;
}
