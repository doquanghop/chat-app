package com.github.doquanghop.chat_app.infrastructure.service;

import com.github.doquanghop.chat_app.infrastructure.model.HasRequestId;

public interface MessagePublisher<T extends HasRequestId> {
    void publish(String channel, T message);
}
