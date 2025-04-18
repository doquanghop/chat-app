package com.github.doquanghop.chat_app.domain.conversation.service;

import com.github.doquanghop.chat_app.domain.conversation.data.dto.request.GetAllConversationRequest;
import com.github.doquanghop.chat_app.domain.conversation.data.dto.response.ConversationResponse;
import com.github.doquanghop.chat_app.infrastructure.model.PageResponse;

public interface ConversationService {
    ConversationResponse getConversation(String conversationId);

    PageResponse<ConversationResponse> getAllConversations(GetAllConversationRequest request);

    void checkParticipantPermission(String conversationId);
}
