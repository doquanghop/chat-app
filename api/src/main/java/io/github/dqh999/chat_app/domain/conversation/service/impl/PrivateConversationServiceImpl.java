package io.github.dqh999.chat_app.domain.conversation.service.impl;

import io.github.dqh999.chat_app.domain.conversation.data.model.Conversation;
import io.github.dqh999.chat_app.domain.conversation.data.model.ConversationType;
import io.github.dqh999.chat_app.domain.conversation.data.model.ParticipantRole;
import io.github.dqh999.chat_app.domain.conversation.service.PrivateConversationService;
import io.github.dqh999.chat_app.infrastructure.util.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("PrivateConversationService")
public class PrivateConversationServiceImpl extends AbstractConversation implements PrivateConversationService {
    @Override
    @Transactional
    public Conversation getPrivateConversationWith(String targetAccountId) {
        String currentAccountId = "";
        Optional<Conversation> optConv = conversationRepository.findPrivateConversationBetween(currentAccountId, targetAccountId);
        if (optConv.isEmpty()) {
            Map<String, ParticipantRole> participants = new HashMap<>();
            participants.put(targetAccountId, ParticipantRole.USER);
            participants.put(currentAccountId, ParticipantRole.USER);
            createAndSave(ConversationType.PRIVATE, "", participants);
        }
        return optConv.get();
    }

    @Override
    public PageResponse<Conversation> getAllConversations(int page, int size) {
        return super.getAllConversations(page, size);
    }

}
