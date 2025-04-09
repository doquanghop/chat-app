package io.github.dqh999.chat_app.infrastructure.service;

public interface MessagePublisher {
    void publish(String channel, Object message);
}
