package io.github.doquanghop.chat_app.domain.message.service.impl;

import io.github.doquanghop.chat_app.application.fliter.RequestIdFilter;
import io.github.doquanghop.chat_app.domain.conversation.data.dto.ConversationEvent;
import io.github.doquanghop.chat_app.domain.conversation.service.ConversationService;
import io.github.doquanghop.chat_app.domain.conversation.service.ParticipantService;
import io.github.doquanghop.chat_app.domain.message.data.dto.request.GetMessageRequest;
import io.github.doquanghop.chat_app.domain.message.data.dto.request.SendMessageRequest;
import io.github.doquanghop.chat_app.domain.message.data.dto.response.MessageResponse;
import io.github.doquanghop.chat_app.domain.message.data.model.Message;
import io.github.doquanghop.chat_app.domain.message.data.repository.MessageRepository;
import io.github.doquanghop.chat_app.domain.message.service.MessageService;
import io.github.doquanghop.chat_app.domain.user.service.UserService;
import io.github.doquanghop.chat_app.infrastructure.service.MessagePublisher;
import io.github.doquanghop.chat_app.infrastructure.utils.RedisChannelUtils;
import io.github.doquanghop.chat_app.infrastructure.model.PageResponse;
import io.github.doquanghop.chat_app.infrastructure.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MessagePublisher<ConversationEvent<?>> messagePublisher;
    private final UserService userService;
    private final ConversationService conversationService;
    private final ParticipantService participantService;
    private final MessageRepository messageRepository;

    public MessageServiceImpl(
            @Qualifier("RedisMessagePublisher") MessagePublisher<ConversationEvent<?>> messagePublisher,
            UserService userService,
            ConversationService conversationService,
            ParticipantService participantService,
            MessageRepository messageRepository) {
        this.messagePublisher = messagePublisher;
        this.userService = userService;
        this.conversationService = conversationService;
        this.participantService = participantService;
        this.messageRepository = messageRepository;
    }


    @Override
    @Transactional
    public MessageResponse send(String conversationId, SendMessageRequest request) {
        log.info("Request send message to conversation {}", conversationId);
        String currentAccountId = SecurityUtil.getCurrentUserId();
        conversationService.checkParticipantPermission(conversationId);
        Message newMessage = Message.builder()
                .content(request.getContent())
                .senderId(currentAccountId)
                .createdAt(LocalDateTime.now())
                .build();
        messageRepository.save(newMessage);
        var sender = userService.getUserById(currentAccountId);
        var response = MessageResponse.builder()
                .id(newMessage.getId())
                .sender(sender)
                .content(newMessage.getContent())
                .build();
        String requestId = RequestIdFilter.getRequestId();
        messagePublisher.publish(
                RedisChannelUtils.buildConversationChannel(conversationId),
                ConversationEvent.<MessageResponse>builder()
                        .requestId(requestId)
                        .type(ConversationEvent.Type.MESSAGE)
                        .payload(response)
                        .build()
        );
        return response;
    }

    @Override
    public PageResponse<MessageResponse> getAllMessages(GetMessageRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPage() - 1, request.getPageSize());
        var messagePage = messageRepository.findAllByConversationId(request.getConversationId(), pageRequest);
        var messageResponse = messagePage.getContent().stream().map(message -> {
            var sender = userService.getUserById(message.getSenderId());
            return MessageResponse.builder()
                    .id(message.getId())
                    .sender(sender)
                    .content(message.getContent())
                    .createdAt(message.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
        if (request.getPage() == 1 && messagePage.getTotalElements() > 0) {
            participantService.updateLastSeenMessage(request.getConversationId(), messagePage.getContent().getFirst().getId());
        }
        return PageResponse.<MessageResponse>builder()
                .data(messageResponse)
                .build();
    }

}
