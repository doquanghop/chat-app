package com.github.doquanghop.chat_app.infrastructure.service.impl;

import com.github.doquanghop.chat_app.infrastructure.annotation.logging.ActionLog;
import com.github.doquanghop.chat_app.infrastructure.constant.QualifierNames;
import com.github.doquanghop.chat_app.infrastructure.service.MessagePublisher;
import com.github.doquanghop.chat_app.infrastructure.model.HasRequestId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service(QualifierNames.REDIS_MESSAGE_PUBLISHER)
@RequiredArgsConstructor
@Slf4j
public class RedisMessagePublisherImpl<T extends HasRequestId> implements MessagePublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @ActionLog(message = "Publishing Redis message")
    public void publish(String channel, T message) {
        redisTemplate.convertAndSend(channel, message);
    }
}
