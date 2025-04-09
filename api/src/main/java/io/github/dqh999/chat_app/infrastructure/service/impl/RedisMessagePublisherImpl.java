package io.github.dqh999.chat_app.infrastructure.service.impl;

import io.github.dqh999.chat_app.infrastructure.service.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisMessagePublisherImpl implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }
}
