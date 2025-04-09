package io.github.dqh999.chat_app.domain.message.service.impl;

import io.github.dqh999.chat_app.domain.conversation.data.dto.ConversationEvent;
import io.github.dqh999.chat_app.domain.conversation.service.ConversationService;
import io.github.dqh999.chat_app.domain.message.data.dto.request.SendMessageRequest;
import io.github.dqh999.chat_app.domain.message.data.dto.response.MessageResponse;
import io.github.dqh999.chat_app.domain.message.data.model.Message;
import io.github.dqh999.chat_app.domain.message.data.repository.MessageRepository;
import io.github.dqh999.chat_app.domain.message.service.MessageService;
import io.github.dqh999.chat_app.infrastructure.model.AppException;
import io.github.dqh999.chat_app.infrastructure.service.MessagePublisher;
import io.github.dqh999.chat_app.infrastructure.util.ChannelUtils;
import io.github.dqh999.chat_app.infrastructure.util.PageResponse;
import io.github.dqh999.chat_app.infrastructure.util.ResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessagePublisher messagePublisher;
    private final ConversationService conversationService;
    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public Message send(SendMessageRequest request) {
        if (!conversationService.canSendMessage(request.getConversationId(), "")) {
            throw new AppException(ResourceException.ACCESS_DENIED);
        }
        Message message = Message.builder()
                .content(request.getContent())
                .senderId("")
                .createdAt(LocalDateTime.now())
                .build();
        messageRepository.save(message);
        messagePublisher.publish(
                ChannelUtils.buildConversationChannel(request.getConversationId()),
                ConversationEvent.<MessageResponse>builder()
                        .type(ConversationEvent.Type.MESSAGE)
                        .payload(MessageResponse.builder()
                                .id(message.getId())
                                .senderId(message.getSenderId())
                                .content(message.getContent())
                                .build())
                        .build()
        );
        return message;
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
