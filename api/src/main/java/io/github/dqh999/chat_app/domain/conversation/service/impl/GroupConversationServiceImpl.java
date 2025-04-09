package io.github.dqh999.chat_app.domain.conversation.service.impl;

import io.github.dqh999.chat_app.domain.conversation.data.dto.request.CreateConversationRequest;
import io.github.dqh999.chat_app.domain.conversation.data.model.Conversation;
import io.github.dqh999.chat_app.domain.conversation.data.model.ConversationType;
import io.github.dqh999.chat_app.domain.conversation.data.model.ParticipantRole;
import io.github.dqh999.chat_app.domain.conversation.service.GroupConversationService;
import io.github.dqh999.chat_app.infrastructure.constant.QualifierNames;
import io.github.dqh999.chat_app.infrastructure.utils.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service(QualifierNames.GROUP_CONVERSATION_SERVICE)
public class GroupConversationServiceImpl extends AbstractConversation implements GroupConversationService {

    @Override
    @Transactional
    public Conversation create(CreateConversationRequest request) {
        Map<String, ParticipantRole> participants = request.getParticipantIds().stream()
                .collect(Collectors.toMap(id -> id, id -> ParticipantRole.MEMBER));
        participants.put("", ParticipantRole.ADMIN);
        return createAndSave(ConversationType.GROUP, request.getName(), participants);
    }

    @Override
    public void leftConversation(String conversationId) {

    }

    @Override
    public void addParticipant(String conversationId, String participantId) {

    }

    @Override
    public void removeParticipant(String conversationId, String participantId) {

    }

    @Override
    public PageResponse<Conversation> getAllConversations(int page, int size) {
        return super.getAllConversations(page, size);
    }
}
