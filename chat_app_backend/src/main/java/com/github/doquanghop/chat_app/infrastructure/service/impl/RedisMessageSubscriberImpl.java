package com.github.doquanghop.chat_app.infrastructure.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.doquanghop.chat_app.infrastructure.service.ChannelHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RedisMessageSubscriberImpl implements MessageListener {
    private final Map<String, ChannelHandler<?>> handlerMap;
    private final ObjectMapper objectMapper;

    public RedisMessageSubscriberImpl(
            List<ChannelHandler<?>> handlers,
            ObjectMapper objectMapper
            ) {
        handlerMap = handlers.stream()
                .collect(Collectors.toMap(ChannelHandler::getChannelPattern, h -> h));
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        log.info("Received message on channel: {}", channel);
        boolean matched = false;

        for (Map.Entry<String, ChannelHandler<?>> entry : handlerMap.entrySet()) {
            String patternKey = entry.getKey();
            ChannelHandler<?> handler = entry.getValue();

            if (channel.matches(patternKey.replace("*", ".*"))) {
                matched = true;
                try {
                    Class<?> payloadType = handler.getPayloadType();
                    Object parsed = objectMapper.readValue(body, payloadType);

                    invokeHandler(handler, channel, parsed);
                } catch (Exception e) {
                    log.error("Failed to deserialize message for channel {}: {}", channel, e.getMessage(), e);
                }
            }
        }

        if (!matched) {
            log.warn("No handler matched for channel: {}", channel);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void invokeHandler(ChannelHandler<T> handler, String channel, Object message) {
        handler.handle(channel, (T) message);
    }
}
