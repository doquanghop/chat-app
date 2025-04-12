package io.github.doquanghop.chat_app.domain.conversation.service;

import io.github.doquanghop.chat_app.domain.conversation.data.dto.request.GetAllConversationRequest;
import io.github.doquanghop.chat_app.domain.conversation.data.dto.response.ConversationResponse;
import io.github.doquanghop.chat_app.domain.conversation.data.model.ConversationType;
import io.github.doquanghop.chat_app.domain.message.data.model.Message;
import io.github.doquanghop.chat_app.infrastructure.model.PageResponse;

public interface ConversationService {
    ConversationResponse getConversation(String conversationId);

    PageResponse<ConversationResponse> getAllConversations(GetAllConversationRequest request);

    void checkParticipantPermission(String conversationId);
}
