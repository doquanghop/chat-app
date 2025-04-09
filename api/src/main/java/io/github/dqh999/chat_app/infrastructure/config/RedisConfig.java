package io.github.dqh999.chat_app.infrastructure.config;

import io.github.dqh999.chat_app.infrastructure.service.ChannelHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheConfiguration cacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            List<ChannelHandler<?>> handlers
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        handlers.forEach(handler -> {
            MessageListenerAdapter adapter = new MessageListenerAdapter() {
                @Override
                public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
                    try {
                        String channel = new String(message.getChannel());
                        Class<?> payloadType = handler.getPayloadType();
                        Object body = new GenericJackson2JsonRedisSerializer().deserialize(message.getBody(), payloadType);

                        if (!payloadType.isInstance(body)) {
                            log.error("Message type mismatch for handler {}: expected {}, got {}",
                                    handler.getClass().getSimpleName(), payloadType, body.getClass());
                            return;
                        }

                        @SuppressWarnings("unchecked")
                        ChannelHandler<Object> objectHandler = (ChannelHandler<Object>) handler;
                        objectHandler.handle(channel, body);

                    } catch (Exception e) {
                        log.error("Error handling Redis message: ", e);
                    }
                }
            };

            container.addMessageListener(
                    adapter,
                    new PatternTopic(handler.getChannelPattern())
            );
        });
        return container;
    }
}
