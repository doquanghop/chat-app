package com.github.doquanghop.chat_app.infrastructure.service;

public interface ChannelHandler<T> {
    String getChannelPattern();

    void handle(String channel, T message);

    Class<T> getPayloadType();
}
