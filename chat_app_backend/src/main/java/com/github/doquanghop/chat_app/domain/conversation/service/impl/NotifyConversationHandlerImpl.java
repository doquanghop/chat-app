package com.github.doquanghop.chat_app.domain.conversation.service.impl;

import com.github.doquanghop.chat_app.domain.conversation.data.dto.ConversationEvent;
import com.github.doquanghop.chat_app.domain.conversation.service.ParticipantService;
import com.github.doquanghop.chat_app.infrastructure.constant.QualifierNames;
import com.github.doquanghop.chat_app.infrastructure.service.BaseChannelHandler;
import com.github.doquanghop.chat_app.infrastructure.service.MessagePublisher;
import com.github.doquanghop.chat_app.infrastructure.utils.RedisChannelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NotifyConversationHandlerImpl extends BaseChannelHandler<ConversationEvent<?>> {
    private final ParticipantService participantService;
    private final MessagePublisher<ConversationEvent<?>> messagePublisher;

    public NotifyConversationHandlerImpl(
            ParticipantService participantService,
            @Qualifier(QualifierNames.WEBSOCKET_MESSAGE_PUBLISHER) MessagePublisher<ConversationEvent<?>> messagePublisher
    ) {
        this.participantService = participantService;
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void doHandle(String channel, ConversationEvent<?> message) {
        String conversationId = RedisChannelUtils.extractConversationId(channel);
        log.info("Handling event [{}] for conversation [{}]", message.getType(), conversationId);
        List<String> participantIds = participantService.getParticipantIdsByConversationId(conversationId);
        if (participantIds.isEmpty()) {
            log.warn("No participants found for conversationId={}, skipping event processing", conversationId);
        }
        log.info("Notifying [{}] participants for conversation [{}]", participantIds.size(), conversationId);
        for (String participantId : participantIds) {
            try {
                messagePublisher.publish("chat.conversation." + participantId, message);
                log.debug("Sent notification to online user [{}] for conversation [{}]", participantId, conversationId);

            } catch (Exception e) {
                log.error("Failed to publish message to user [{}] for conversation [{}]", participantId, conversationId, e);
            }
        }
    }

    @Override
    public String getChannelPattern() {
        return RedisChannelUtils.CONVERSATION_PATTERN;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<ConversationEvent<?>> getPayloadType() {
        return (Class<ConversationEvent<?>>) (Class<?>) ConversationEvent.class;
    }
}
