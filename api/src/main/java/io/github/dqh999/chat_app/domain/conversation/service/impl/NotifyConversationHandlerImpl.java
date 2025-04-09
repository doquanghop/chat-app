package io.github.dqh999.chat_app.domain.conversation.service.impl;

import io.github.dqh999.chat_app.domain.conversation.data.dto.ConversationEvent;
import io.github.dqh999.chat_app.infrastructure.service.ChannelHandler;
import io.github.dqh999.chat_app.infrastructure.util.ChannelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotifyConversationHandlerImpl implements ChannelHandler<ConversationEvent<?>> {
    @Override
    public String getChannelPattern() {
        return ChannelUtils.CONVERSATION_PATTERN;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<ConversationEvent<?>> getPayloadType() {
        return (Class<ConversationEvent<?>>) (Class<?>) ConversationEvent.class;
    }

    @Override
    public void handle(String channel, ConversationEvent<?> message) {
        String conversationId = ChannelUtils.extractConversationId(channel);
        log.info("Received message on channel {} (conversationId: {}): {}", channel, conversationId, message);
    }

}
