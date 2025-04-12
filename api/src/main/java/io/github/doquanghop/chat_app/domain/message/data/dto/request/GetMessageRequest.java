package io.github.doquanghop.chat_app.domain.message.data.dto.request;

import io.github.doquanghop.chat_app.infrastructure.model.BasePaginationRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class GetMessageRequest extends BasePaginationRequest {
    private String conversationId;
    private String keyword;
}
