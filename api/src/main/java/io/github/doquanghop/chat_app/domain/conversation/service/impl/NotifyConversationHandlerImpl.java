package io.github.doquanghop.chat_app.domain.conversation.service.impl;

import io.github.doquanghop.chat_app.domain.conversation.data.dto.ConversationEvent;
import io.github.doquanghop.chat_app.domain.conversation.service.ParticipantService;
import io.github.doquanghop.chat_app.infrastructure.constant.QualifierNames;
import io.github.doquanghop.chat_app.infrastructure.service.ChannelHandler;
import io.github.doquanghop.chat_app.infrastructure.service.MessagePublisher;
import io.github.doquanghop.chat_app.infrastructure.utils.RedisChannelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotifyConversationHandlerImpl implements ChannelHandler<ConversationEvent<?>> {
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
    public void handle(String channel, ConversationEvent<?> message) {
        String conversationId = RedisChannelUtils.extractConversationId(channel);
        log.info("Handling event [{}] for conversation [{}], requestId = [{}]", message.getType(), conversationId, message.getRequestId());
        List<String> participantIds = participantService.getParticipantIdsByConversationId(conversationId);
        if (participantIds.isEmpty()) {
            log.warn("No participants found for conversationId={}, skipping event processing", conversationId);
        }
        ;
        log.info("Notifying [{}] participants for conversation [{}], requestId = [{}]", participantIds.size(), conversationId, message.getRequestId());
        for (String participantId : participantIds) {
            try {
                messagePublisher.publish("chat.conversation." + participantId, message);
                log.debug("Sent notification to online user [{}] for conversation [{}], requestId = [{}]", participantId, conversationId, message.getRequestId());

            } catch (Exception e) {
                log.error("Failed to publish message to user [{}] for conversation [{}, requestId = [{}]]", participantId, conversationId, message.getRequestId(), e);
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
