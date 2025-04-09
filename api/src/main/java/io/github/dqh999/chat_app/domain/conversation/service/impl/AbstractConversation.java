package io.github.dqh999.chat_app.domain.conversation.service.impl;

import io.github.dqh999.chat_app.domain.conversation.data.model.Conversation;
import io.github.dqh999.chat_app.domain.conversation.data.model.ConversationType;
import io.github.dqh999.chat_app.domain.conversation.data.model.Participant;
import io.github.dqh999.chat_app.domain.conversation.data.model.ParticipantRole;
import io.github.dqh999.chat_app.domain.conversation.data.repository.ConversationRepository;
import io.github.dqh999.chat_app.domain.conversation.data.repository.ParticipantRepository;
import io.github.dqh999.chat_app.domain.conversation.service.ConversationService;
import io.github.dqh999.chat_app.domain.message.data.model.Message;
import io.github.dqh999.chat_app.infrastructure.model.AppException;
import io.github.dqh999.chat_app.infrastructure.util.PageResponse;
import io.github.dqh999.chat_app.infrastructure.util.ResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public abstract class AbstractConversation implements ConversationService {
    @Autowired
    protected ConversationRepository conversationRepository;
    @Autowired
    protected ParticipantRepository participantRepository;

    @Override
    public PageResponse<Conversation> getAllConversations(int page, int size) {
        String currentAccountId = "";
        PageRequest pageRequest = PageRequest.of(page, size);
        var conversationPage = conversationRepository.findAllConversation(currentAccountId, pageRequest);
        conversationPage.getContent().forEach(conversation -> {
            return;
        });
        return PageResponse.build(conversationPage, conversationPage.getContent());
    }

    @Override
    public boolean canSendMessage(String conversationId, String senderId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new AppException(ResourceException.ENTITY_NOT_FOUND);
        }
        return false;
    }

    @Override
    public void sendNotification(String conversationId, Message message) {

    }

    private List<String> getParticipant(String conversationId) {
        return participantRepository.findAllAccountIdsByConversationId(conversationId);
    }

    protected Conversation createAndSave(ConversationType type, String name, Map<String, ParticipantRole> participants) {
        Conversation conversation = Conversation.builder()
                .type(type)
                .name(name)
                .build();
        conversationRepository.save(conversation);
        for (Map.Entry<String, ParticipantRole> entry : participants.entrySet()) {
            Participant participant = Participant.builder()
                    .conversationId(conversation.getId())
                    .accountId(entry.getKey())
                    .role(entry.getValue())
                    .joinedAt(LocalDateTime.now())
                    .build();
            participantRepository.save(participant);
        }
        return conversation;
    }
}
