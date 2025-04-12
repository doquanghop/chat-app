package io.github.doquanghop.chat_app.domain.conversation.service.impl;

import io.github.doquanghop.chat_app.domain.conversation.data.dto.ConversationEvent;
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
import io.github.doquanghop.chat_app.infrastructure.constant.QualifierNames;
import io.github.doquanghop.chat_app.infrastructure.model.AppException;
import io.github.doquanghop.chat_app.infrastructure.model.PageResponse;
import io.github.doquanghop.chat_app.infrastructure.service.MessagePublisher;
import io.github.doquanghop.chat_app.infrastructure.utils.ResourceException;
import io.github.doquanghop.chat_app.infrastructure.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractConversation implements ConversationService {
    @Autowired
    protected UserService userService;
    @Autowired
    protected ConversationRepository conversationRepository;

    @Autowired
    protected ParticipantService participantService;

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
        log.info("Fetching all conversations for userId={}, page=[{}], size={}", currentAccountId, request.getPage(), request.getPageSize());
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
            log.warn("Conversation with ID [{}] not found", conversationId);
            throw new AppException(ResourceException.ENTITY_NOT_FOUND);
        }
        if (participantService.checkParticipantPermission(conversationId)) {
            log.warn("User does not have permission to access conversation with ID [{}]", conversationId);
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

    private ConversationResponse buildConversationResponse(Conversation conversation) {
        ConversationResponse response = ConversationResponse.builder()
                .id(conversation.getId())
                .type(conversation.getType())
                .build();
        if (conversation.getType() == ConversationType.PRIVATE) {
            Participant otherParticipant = participantService.getOtherParticipantInPrivateConversation(conversation.getId());
            response.setName(otherParticipant.getNickname());
        } else {
            response.setName(conversation.getName());
            response.setAvatarURL(conversation.getAvatarURL());
        }
        return response;
    }
}
