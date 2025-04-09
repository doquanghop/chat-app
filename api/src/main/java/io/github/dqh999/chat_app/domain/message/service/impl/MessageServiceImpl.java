package io.github.dqh999.chat_app.domain.message.service.impl;

import io.github.dqh999.chat_app.application.fliter.RequestIdFilter;
import io.github.dqh999.chat_app.domain.conversation.data.dto.ConversationEvent;
import io.github.dqh999.chat_app.domain.conversation.service.ConversationService;
import io.github.dqh999.chat_app.domain.message.data.dto.request.SendMessageRequest;
import io.github.dqh999.chat_app.domain.message.data.dto.response.MessageResponse;
import io.github.dqh999.chat_app.domain.message.data.model.Message;
import io.github.dqh999.chat_app.domain.message.data.repository.MessageRepository;
import io.github.dqh999.chat_app.domain.message.service.MessageService;
import io.github.dqh999.chat_app.infrastructure.service.MessagePublisher;
import io.github.dqh999.chat_app.infrastructure.utils.RedisChannelUtils;
import io.github.dqh999.chat_app.infrastructure.utils.PageResponse;
import io.github.dqh999.chat_app.infrastructure.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MessagePublisher messagePublisher;
    private final ConversationService conversationService;
    private final MessageRepository messageRepository;

    public MessageServiceImpl(
            @Qualifier("RedisMessagePublisher") MessagePublisher messagePublisher,
            ConversationService conversationService,
            MessageRepository messageRepository) {
        this.messagePublisher = messagePublisher;
        this.conversationService = conversationService;
        this.messageRepository = messageRepository;
    }


    @Override
    @Transactional
    public MessageResponse send(SendMessageRequest request) {
        log.info("Request send message to conversation {}", request.getConversationId());
        String currentAccountId = SecurityUtil.getCurrentUserId();
        conversationService.checkSenderPermission(request.getConversationId(), currentAccountId);
        Message newMessage = Message.builder()
                .content(request.getContent())
                .senderId(currentAccountId)
                .createdAt(LocalDateTime.now())
                .build();
        messageRepository.save(newMessage);
        var response = MessageResponse.builder()
                .id(newMessage.getId())
                .senderId(newMessage.getSenderId())
                .content(newMessage.getContent())
                .build();
        String requestId = RequestIdFilter.getRequestId();
        messagePublisher.publish(
                RedisChannelUtils.buildConversationChannel(request.getConversationId()),
                ConversationEvent.<MessageResponse>builder()
                        .requestId(requestId)
                        .type(ConversationEvent.Type.MESSAGE)
                        .payload(response)
                        .build()
        );
        return response;
    }

    @Override
    public PageResponse<Message> getAllMessages(String conversationId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        var messagePage = messageRepository.findAllByConversationId(conversationId, pageRequest);
        return PageResponse.build(messagePage, messagePage.getContent());
    }

    @Override
    public PageResponse<Message> search(String conversationId, String keyword, int page, int size) {
        return null;
    }
}
