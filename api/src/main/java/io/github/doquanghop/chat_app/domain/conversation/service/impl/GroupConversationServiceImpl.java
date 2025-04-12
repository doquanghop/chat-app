package io.github.doquanghop.chat_app.domain.conversation.service.impl;

import io.github.doquanghop.chat_app.domain.conversation.data.dto.request.CreateConversationGroupRequest;
import io.github.doquanghop.chat_app.domain.conversation.data.dto.request.GetAllConversationRequest;
import io.github.doquanghop.chat_app.domain.conversation.data.dto.response.ConversationResponse;
import io.github.doquanghop.chat_app.domain.conversation.data.model.Conversation;
import io.github.doquanghop.chat_app.domain.conversation.data.model.ConversationType;
import io.github.doquanghop.chat_app.domain.conversation.data.model.ParticipantRole;
import io.github.doquanghop.chat_app.domain.conversation.service.GroupConversationService;
import io.github.doquanghop.chat_app.infrastructure.constant.QualifierNames;
import io.github.doquanghop.chat_app.infrastructure.model.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service(QualifierNames.GROUP_CONVERSATION_SERVICE)
public class GroupConversationServiceImpl extends AbstractConversation implements GroupConversationService {

    @Override
    public ConversationResponse getConversation(String conversationId) {
        return super.getConversation(conversationId);
    }

    @Override
    @Transactional
    public Conversation create(CreateConversationGroupRequest request) {
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
    public PageResponse<ConversationResponse> getAllConversations(GetAllConversationRequest request) {
        request.setType(ConversationType.GROUP);
        return super.getAllConversations(request);
    }
}
