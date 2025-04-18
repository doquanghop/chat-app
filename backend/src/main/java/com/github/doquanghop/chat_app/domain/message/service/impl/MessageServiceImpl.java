package com.github.doquanghop.chat_app.domain.message.service.impl;

import com.github.doquanghop.chat_app.application.fliter.RequestIdFilter;
import com.github.doquanghop.chat_app.domain.conversation.data.dto.ConversationEvent;
import com.github.doquanghop.chat_app.domain.conversation.service.ConversationService;
import com.github.doquanghop.chat_app.domain.conversation.service.ParticipantService;
import com.github.doquanghop.chat_app.domain.message.data.dto.request.GetMessageRequest;
import com.github.doquanghop.chat_app.domain.message.data.dto.request.SendMessageRequest;
import com.github.doquanghop.chat_app.domain.message.data.dto.response.MessageResponse;
import com.github.doquanghop.chat_app.domain.message.data.model.Message;
import com.github.doquanghop.chat_app.domain.message.data.repository.MessageRepository;
import com.github.doquanghop.chat_app.domain.message.mapper.MessageMapper;
import com.github.doquanghop.chat_app.domain.message.service.MessageService;
import com.github.doquanghop.chat_app.infrastructure.annotation.logging.ActionLog;
import com.github.doquanghop.chat_app.infrastructure.service.MessagePublisher;
import com.github.doquanghop.chat_app.infrastructure.utils.RedisChannelUtils;
import com.github.doquanghop.chat_app.infrastructure.model.PageResponse;
import com.github.doquanghop.chat_app.infrastructure.security.SecurityUtil;
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
    private final ConversationService conversationService;
    private final ParticipantService participantService;
    private final MessageMapper mapper;
    private final MessageRepository messageRepository;

    public MessageServiceImpl(
            @Qualifier("RedisMessagePublisher") MessagePublisher<ConversationEvent<?>> messagePublisher,
            ConversationService conversationService,
            ParticipantService participantService,
            MessageMapper mapper,
            MessageRepository messageRepository) {
        this.messagePublisher = messagePublisher;
        this.conversationService = conversationService;
        this.participantService = participantService;
        this.mapper = mapper;
        this.messageRepository = messageRepository;
    }


    @Override
    @Transactional
    @ActionLog(message = "Sending message in conversation")
    public MessageResponse send(String conversationId, SendMessageRequest request) {
        String currentAccountId = SecurityUtil.getCurrentUserId();
        conversationService.checkParticipantPermission(conversationId);
        Message newMessage = Message.builder()
                .conversationId(conversationId)
                .content(request.getContent())
                .senderId(currentAccountId)
                .createdAt(LocalDateTime.now())
                .build();
        messageRepository.save(newMessage);
        var response = mapper.toMessageResponse(newMessage);
        messagePublisher.publish(
                RedisChannelUtils.buildConversationChannel(conversationId),
                ConversationEvent.<MessageResponse>builder()
                        .requestId(RequestIdFilter.getRequestId())
                        .type(ConversationEvent.Type.MESSAGE)
                        .payload(response)
                        .build()
        );
        return response;
    }

    @Override
    @ActionLog(message = "Getting all message")
    public PageResponse<MessageResponse> getAllMessages(GetMessageRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPage() - 1, request.getPageSize());
        var messagePage = messageRepository.findAllByConversationId(request.getConversationId(), pageRequest);
        var messageResponse = messagePage.getContent().stream().map(mapper::toMessageResponse).collect(Collectors.toList());
        if (request.getPage() == 1 && messagePage.getTotalElements() > 0) {
            participantService.updateLastSeenMessage(request.getConversationId(), messagePage.getContent().getFirst().getId());
        }
        return PageResponse.<MessageResponse>builder()
                .data(messageResponse)
                .build();
    }

}
