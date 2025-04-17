package com.github.doquanghop.chat_app.domain.conversation.service.impl;

import com.github.doquanghop.chat_app.domain.account.service.SessionService;
import com.github.doquanghop.chat_app.domain.conversation.data.dto.request.GetAllConversationRequest;
import com.github.doquanghop.chat_app.domain.conversation.data.dto.response.ConversationResponse;
import com.github.doquanghop.chat_app.domain.conversation.data.model.Conversation;
import com.github.doquanghop.chat_app.domain.conversation.data.model.ConversationType;
import com.github.doquanghop.chat_app.domain.conversation.data.model.Participant;
import com.github.doquanghop.chat_app.domain.conversation.data.model.ParticipantRole;
import com.github.doquanghop.chat_app.domain.conversation.data.repository.ConversationRepository;
import com.github.doquanghop.chat_app.domain.conversation.service.ConversationService;
import com.github.doquanghop.chat_app.domain.conversation.service.ParticipantService;
import com.github.doquanghop.chat_app.domain.user.service.UserService;
import com.github.doquanghop.chat_app.infrastructure.model.AppException;
import com.github.doquanghop.chat_app.infrastructure.model.PageResponse;
import com.github.doquanghop.chat_app.infrastructure.model.ResourceException;
import com.github.doquanghop.chat_app.infrastructure.security.SecurityUtil;
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
        var responses = conversationPage.stream().map(this::buildConversationResponse).collect(Collectors.toList());
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
        if (participantService.hasPermission(conversationId)) {
            throw new AppException(ResourceException.ACCESS_DENIED);
        }
    }

    protected Conversation createAndSave(ConversationType type, String name, Map<String, ParticipantRole> participants) {
        Conversation conversation = Conversation.builder()
                .type(type)
                .name(name)
                .build();
        conversationRepository.save(conversation);
        participantService.addParticipants(conversation.getId(), participants);
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
