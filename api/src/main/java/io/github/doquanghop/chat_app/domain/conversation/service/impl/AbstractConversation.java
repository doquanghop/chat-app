package io.github.doquanghop.chat_app.domain.conversation.service.impl;

import io.github.doquanghop.chat_app.domain.account.service.SessionService;
import io.github.doquanghop.chat_app.domain.conversation.data.dto.request.GetAllConversationRequest;
import io.github.doquanghop.chat_app.domain.conversation.data.dto.response.ConversationResponse;
import io.github.doquanghop.chat_app.domain.conversation.data.model.Conversation;
import io.github.doquanghop.chat_app.domain.conversation.data.model.ConversationType;
import io.github.doquanghop.chat_app.domain.conversation.data.model.Participant;
import io.github.doquanghop.chat_app.domain.conversation.data.model.ParticipantRole;
import io.github.doquanghop.chat_app.domain.conversation.data.repository.ConversationRepository;
import io.github.doquanghop.chat_app.domain.conversation.service.ConversationService;
import io.github.doquanghop.chat_app.domain.conversation.service.ParticipantService;
import io.github.doquanghop.chat_app.domain.user.service.UserService;
import io.github.doquanghop.chat_app.infrastructure.model.AppException;
import io.github.doquanghop.chat_app.infrastructure.model.PageResponse;
import io.github.doquanghop.chat_app.infrastructure.utils.ResourceException;
import io.github.doquanghop.chat_app.infrastructure.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractConversation implements ConversationService {
    @Autowired
    protected UserService userService;

    @Autowired
    protected ParticipantService participantService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    protected ConversationRepository conversationRepository;

    @Override
    public ConversationResponse getConversation(String conversationId) {
        checkParticipantPermission(conversationId);
        Conversation existingConversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ResourceException.ENTITY_NOT_FOUND));
        return buildConversationResponse(existingConversation);
    }

    @Override
    public PageResponse<ConversationResponse> getAllConversations(GetAllConversationRequest request) {
        String currentAccountId = SecurityUtil.getCurrentUserId();
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getPageSize());
        var conversationPage = conversationRepository.findAllConversation(request.getType(), currentAccountId, pageRequest);
        var responses = conversationPage.stream().map(conversation -> {
            var response = ConversationResponse.builder().build();
            long unreadCount = participantService.getUnreadMessageCount(conversation.getId());
            response.setUnreadCount(unreadCount);
            return response;
        }).collect(Collectors.toList());
        return PageResponse.<ConversationResponse>builder()
                .hasNext(conversationPage.hasNext())
                .hasPrevious(conversationPage.hasPrevious())
                .data(responses)
                .build();
    }

    @Override
    public void checkParticipantPermission(String conversationId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new AppException(ResourceException.ENTITY_NOT_FOUND);
        }
        if (participantService.checkParticipantPermission(conversationId)) {
            throw new AppException(ResourceException.ACCESS_DENIED);
        }
    }

    protected Conversation createAndSave(ConversationType type, String name, Map<String, ParticipantRole> participants) {
        Conversation conversation = Conversation.builder()
                .type(type)
                .name(name)
                .build();
        conversationRepository.save(conversation);
        participantService.createParticipants(conversation.getId(), participants);
        return conversation;
    }

    protected ConversationResponse buildConversationResponse(Conversation conversation) {
        ConversationResponse.ConversationResponseBuilder builder = ConversationResponse.builder()
                .id(conversation.getId())
                .type(conversation.getType())
                .name(conversation.getName())
                .avatarURL(conversation.getAvatarURL());
        if (conversation.getType() == ConversationType.PRIVATE) {
            configurePrivateConversationResponse(builder, conversation);
        }
        return builder.build();
    }

    private void configurePrivateConversationResponse(ConversationResponse.ConversationResponseBuilder builder,
                                                      Conversation conversation) {
        Participant counterpart = participantService.getOtherParticipantInPrivateConversation(conversation.getId());
        var counterpartUser = userService.getUserById(counterpart.getAccountId());
        var counterPartStatus = sessionService.getSessionStatus(counterpart.getAccountId());

        builder.name(counterpart.getNickname() != null ? counterpart.getNickname() : counterpartUser.getFullName())
                .avatarURL(counterpartUser.getAvatarURL())
                .isOnline(counterPartStatus.isOnline())
                .lastSeen(counterPartStatus.lastSeen());
    }
}
