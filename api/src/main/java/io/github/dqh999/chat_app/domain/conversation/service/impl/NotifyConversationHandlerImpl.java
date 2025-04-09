package io.github.dqh999.chat_app.domain.conversation.service.impl;

import io.github.dqh999.chat_app.domain.conversation.data.dto.ConversationEvent;
import io.github.dqh999.chat_app.infrastructure.service.ChannelHandler;
import io.github.dqh999.chat_app.infrastructure.utils.RedisChannelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotifyConversationHandlerImpl implements ChannelHandler<ConversationEvent<?>> {
    @Override
    public String getChannelPattern() {
        return RedisChannelUtils.CONVERSATION_PATTERN;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<ConversationEvent<?>> getPayloadType() {
        return (Class<ConversationEvent<?>>) (Class<?>) ConversationEvent.class;
    }

    @Override
    public void handle(String channel, ConversationEvent<?> message) {
        String conversationId = RedisChannelUtils.extractConversationId(channel);
        String requestId = message.getRequestId();
        ConversationEvent.Type type = message.getType();
        log.info("Handling event [{}] for conversation [{}], requestId={}", type, conversationId, requestId);
    }

}
