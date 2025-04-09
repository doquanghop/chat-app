package io.github.dqh999.chat_app.domain.conversation.service.impl;

import io.github.dqh999.chat_app.domain.conversation.data.model.Conversation;
import io.github.dqh999.chat_app.domain.conversation.data.model.ConversationType;
import io.github.dqh999.chat_app.domain.conversation.data.model.ParticipantRole;
import io.github.dqh999.chat_app.domain.conversation.service.PrivateConversationService;
import io.github.dqh999.chat_app.infrastructure.constant.QualifierNames;
import io.github.dqh999.chat_app.infrastructure.utils.PageResponse;
import io.github.dqh999.chat_app.infrastructure.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(QualifierNames.PRIVATE_CONVERSATION_SERVICE)
public class PrivateConversationServiceImpl extends AbstractConversation implements PrivateConversationService {
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
    public PageResponse<Conversation> getAllConversations(int page, int size) {
        return super.getAllConversations(page, size);
    }

}
