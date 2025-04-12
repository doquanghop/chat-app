package io.github.doquanghop.chat_app.infrastructure.service;

import io.github.doquanghop.chat_app.infrastructure.model.HasRequestId;

public interface MessagePublisher<T extends HasRequestId> {
    void publish(String channel, T message);
}
