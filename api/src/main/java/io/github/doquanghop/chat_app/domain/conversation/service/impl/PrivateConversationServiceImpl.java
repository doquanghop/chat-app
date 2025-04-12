package io.github.doquanghop.chat_app.domain.conversation.service.impl;

import io.github.doquanghop.chat_app.domain.conversation.data.dto.request.GetAllConversationRequest;
import io.github.doquanghop.chat_app.domain.conversation.data.dto.response.ConversationResponse;
import io.github.doquanghop.chat_app.domain.conversation.data.model.Conversation;
import io.github.doquanghop.chat_app.domain.conversation.data.model.ConversationType;
import io.github.doquanghop.chat_app.domain.conversation.data.model.ParticipantRole;
import io.github.doquanghop.chat_app.domain.conversation.service.PrivateConversationService;
import io.github.doquanghop.chat_app.domain.message.data.model.Message;
import io.github.doquanghop.chat_app.infrastructure.constant.QualifierNames;
import io.github.doquanghop.chat_app.infrastructure.model.PageResponse;
import io.github.doquanghop.chat_app.infrastructure.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(QualifierNames.PRIVATE_CONVERSATION_SERVICE)
public class PrivateConversationServiceImpl extends AbstractConversation
        implements PrivateConversationService {
    @Override
    public ConversationResponse getConversation(String conversationId) {
        return super.getConversation(conversationId);
    }


    @Override
    @Transactional
    public Conversation getPrivateConversationWith(String targetAccountId) {
        String currentAccountId = SecurityUtil.getCurrentUserId();

        Optional<Conversation> existingConversation =
                conversationRepository.findPrivateConversationBetween(currentAccountId, targetAccountId);

        Conversation conversation = existingConversation.orElseGet(() -> {
            Map<String, ParticipantRole> participants = new HashMap<>();
            participants.put(currentAccountId, ParticipantRole.USER);
            participants.put(targetAccountId, ParticipantRole.USER);
            return createAndSave(ConversationType.PRIVATE, "", participants);
        });

        if (conversation.getName() == null || conversation.getAvatarURL() == null) {
            var targetUser = userService.getUserById(targetAccountId);
            conversation.setName(targetUser.getFullName());
            conversation.setAvatarURL(targetUser.getAvatarURL());
        }

        return conversation;
    }

    @Override
    public PageResponse<ConversationResponse> getAllConversations(GetAllConversationRequest request) {
        request.setType(ConversationType.PRIVATE);
        return super.getAllConversations(request);
    }

}
