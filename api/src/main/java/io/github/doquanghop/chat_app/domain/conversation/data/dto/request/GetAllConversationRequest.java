package io.github.doquanghop.chat_app.domain.conversation.data.dto.request;

import io.github.doquanghop.chat_app.domain.conversation.data.model.ConversationType;
import io.github.doquanghop.chat_app.infrastructure.model.BasePaginationRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class GetAllConversationRequest extends BasePaginationRequest {
    private ConversationType type;
}
