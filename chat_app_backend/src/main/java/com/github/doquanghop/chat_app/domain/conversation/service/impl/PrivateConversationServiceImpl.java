package com.github.doquanghop.chat_app.domain.conversation.service.impl;

import com.github.doquanghop.chat_app.domain.conversation.data.dto.request.GetAllConversationRequest;
import com.github.doquanghop.chat_app.domain.conversation.data.dto.response.ConversationResponse;
import com.github.doquanghop.chat_app.domain.conversation.data.model.Conversation;
import com.github.doquanghop.chat_app.domain.conversation.data.model.ConversationType;
import com.github.doquanghop.chat_app.domain.conversation.data.model.ParticipantRole;
import com.github.doquanghop.chat_app.domain.conversation.service.PrivateConversationService;
import com.github.doquanghop.chat_app.infrastructure.constant.QualifierNames;
import com.github.doquanghop.chat_app.infrastructure.model.PageResponse;
import com.github.doquanghop.chat_app.infrastructure.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(QualifierNames.PRIVATE_CONVERSATION_SERVICE)
@RequiredArgsConstructor
public class PrivateConversationServiceImpl extends AbstractConversation
        implements PrivateConversationService {

    @Override
    public ConversationResponse getConversation(String conversationId) {
        return super.getConversation(conversationId);
    }

    @Override
    @Transactional
    public ConversationResponse getPrivateConversationWith(String counterpartId) {
        String currentAccountId = SecurityUtil.getCurrentUserId();

        Optional<Conversation> existingConversation =
                conversationRepository.findPrivateConversationBetween(currentAccountId, counterpartId);

        Conversation conversation = existingConversation.orElseGet(() -> {
            Map<String, ParticipantRole> participants = new HashMap<>();
            participants.put(currentAccountId, ParticipantRole.USER);
            participants.put(counterpartId, ParticipantRole.USER);
            return createAndSave(ConversationType.PRIVATE, "", participants);
        });
        return buildConversationResponse(conversation);
    }

    @Override
    public PageResponse<ConversationResponse> getAllConversations(GetAllConversationRequest request) {
        request.setType(ConversationType.PRIVATE);
        return super.getAllConversations(request);
    }

}
