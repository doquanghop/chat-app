package io.github.doquanghop.chat_app.domain.conversation.service;

import io.github.doquanghop.chat_app.domain.conversation.data.dto.response.ConversationResponse;

public interface PrivateConversationService extends ConversationService {
    ConversationResponse getPrivateConversationWith(String counterpartId);
}
