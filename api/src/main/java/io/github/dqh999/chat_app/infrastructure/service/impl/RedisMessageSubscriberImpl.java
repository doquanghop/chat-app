package io.github.dqh999.chat_app.infrastructure.service.impl;

import io.github.dqh999.chat_app.infrastructure.service.ChannelHandler;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RedisMessageSubscriberImpl implements MessageListener {
    private final Map<String, ChannelHandler> handlerMap;

    public RedisMessageSubscriberImpl(List<ChannelHandler> handlers) {
        handlerMap = handlers.stream()
                .collect(Collectors.toMap(ChannelHandler::getChannelPattern, h -> h));
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        handlerMap.forEach((patternKey, handler) -> {
            if (channel.matches(patternKey.replace("*", ".*"))) {
                handler.handle(channel, body);
            }
        });

    }
}
