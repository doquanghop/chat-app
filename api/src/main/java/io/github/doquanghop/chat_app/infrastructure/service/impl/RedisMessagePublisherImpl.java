package io.github.doquanghop.chat_app.infrastructure.service.impl;

import io.github.doquanghop.chat_app.infrastructure.constant.QualifierNames;
import io.github.doquanghop.chat_app.infrastructure.service.MessagePublisher;
import io.github.doquanghop.chat_app.infrastructure.model.HasRequestId;
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
    public void publish(String channel, T message) {
        String payloadClass = message.getClass().getSimpleName();

        try {
            log.info("Publishing Redis message: channel = [{}], payloadClass = [{}]", channel, payloadClass);
            redisTemplate.convertAndSend(channel, message);
            log.info("Successfully published Redis message: channel = [{}]", channel);
        } catch (Exception e) {
            log.error("Failed to publish Redis message: channel = [{}], payloadClass = [{}]", channel, payloadClass, e);
        }
    }
}
