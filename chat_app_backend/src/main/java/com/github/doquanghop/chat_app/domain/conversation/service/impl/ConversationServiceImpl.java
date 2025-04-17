package com.github.doquanghop.chat_app.domain.conversation.service.impl;

import com.github.doquanghop.chat_app.domain.conversation.data.dto.request.GetAllConversationRequest;
import com.github.doquanghop.chat_app.domain.conversation.data.dto.response.ConversationResponse;
import com.github.doquanghop.chat_app.infrastructure.model.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class ConversationServiceImpl extends AbstractConversation {

    @Override
    public ConversationResponse getConversation(String conversationId) {
        return super.getConversation(conversationId);
    }

    @Override
    public PageResponse<ConversationResponse> getAllConversations(GetAllConversationRequest request) {
        return super.getAllConversations(request);
    }

    @Override
    public void checkParticipantPermission(String conversationId) {
        super.checkParticipantPermission(conversationId);
    }

}
