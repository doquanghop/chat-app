package io.github.doquanghop.chat_app.domain.conversation.service.impl;

import io.github.doquanghop.chat_app.application.fliter.RequestIdFilter;
import io.github.doquanghop.chat_app.domain.conversation.data.dto.ConversationEvent;
import io.github.doquanghop.chat_app.domain.conversation.data.dto.request.CreateConversationGroupRequest;
import io.github.doquanghop.chat_app.domain.conversation.data.dto.request.GetAllConversationRequest;
import io.github.doquanghop.chat_app.domain.conversation.data.dto.response.ConversationResponse;
import io.github.doquanghop.chat_app.domain.conversation.data.model.Conversation;
import io.github.doquanghop.chat_app.domain.conversation.data.model.ConversationType;
import io.github.doquanghop.chat_app.domain.conversation.data.model.ParticipantRole;
import io.github.doquanghop.chat_app.domain.conversation.service.GroupConversationService;
import io.github.doquanghop.chat_app.infrastructure.constant.QualifierNames;
import io.github.doquanghop.chat_app.infrastructure.model.PageResponse;
import io.github.doquanghop.chat_app.infrastructure.security.SecurityUtil;
import io.github.doquanghop.chat_app.infrastructure.service.MessagePublisher;
import io.github.doquanghop.chat_app.infrastructure.utils.RedisChannelUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service(QualifierNames.GROUP_CONVERSATION_SERVICE)
public class GroupConversationServiceImpl extends AbstractConversation
        implements GroupConversationService {

    private final MessagePublisher<ConversationEvent<?>> evenPublisher;

    public GroupConversationServiceImpl(@Qualifier(QualifierNames.REDIS_MESSAGE_PUBLISHER) MessagePublisher<ConversationEvent<?>> evenPublisher) {
        this.evenPublisher = evenPublisher;
    }

    @Override
    public ConversationResponse getConversation(String conversationId) {
        return super.getConversation(conversationId);
    }

    @Override
    @Transactional
    public Conversation create(CreateConversationGroupRequest request) {
        String adminId = SecurityUtil.getCurrentUserId();
        Map<String, ParticipantRole> participants = request.getParticipantIds().stream()
                .collect(Collectors.toMap(id -> id, id -> ParticipantRole.MEMBER));
        participants.put(adminId, ParticipantRole.ADMIN);
        Conversation newConversation = createAndSave(ConversationType.GROUP, request.getName(), participants);
        evenPublisher.publish(
                RedisChannelUtils.buildConversationChannel(newConversation.getId()),
                ConversationEvent.<Conversation>builder()
                        .requestId(RequestIdFilter.getRequestId())
                        .type(ConversationEvent.Type.CONVERSATION_UPDATE)
                        .payload(newConversation)
                        .build()
        );
        return newConversation;
    }

    @Override
    public void leftConversation(String conversationId) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        checkParticipantPermission(conversationId);
        participantService.removeParticipant(conversationId, currentUserId);
        evenPublisher.publish(
                RedisChannelUtils.buildConversationChannel(conversationId),
                ConversationEvent.<String>builder()
                        .requestId(RequestIdFilter.getRequestId())
                        .type(ConversationEvent.Type.MEMBER_LEAVE)
                        .payload(currentUserId)
                        .build()
        );
    }

    @Override
    public void addParticipant(String conversationId, String participantId) {
        checkParticipantPermission(conversationId);
        evenPublisher.publish(
                RedisChannelUtils.buildConversationChannel(conversationId),
                ConversationEvent.<String>builder()
                        .requestId(RequestIdFilter.getRequestId())
                        .type(ConversationEvent.Type.MEMBER_JOIN)
                        .payload(participantId)
                        .build()
        );
    }

    @Override
    public void removeParticipant(String conversationId, String participantId) {
        checkParticipantPermission(conversationId);
        participantService.removeParticipant(conversationId, participantId);
        evenPublisher.publish(
                RedisChannelUtils.buildConversationChannel(conversationId),
                ConversationEvent.<String>builder()
                        .requestId(RequestIdFilter.getRequestId())
                        .type(ConversationEvent.Type.MEMBER_REMOVE)
                        .payload(participantId)
                        .build()
        );
    }

    @Override
    public PageResponse<ConversationResponse> getAllConversations(GetAllConversationRequest request) {
        request.setType(ConversationType.GROUP);
        return super.getAllConversations(request);
    }
}
