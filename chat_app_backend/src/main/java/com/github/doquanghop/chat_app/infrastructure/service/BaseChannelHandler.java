package com.github.doquanghop.chat_app.infrastructure.service;

import com.github.doquanghop.chat_app.infrastructure.model.HasRequestId;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public abstract class BaseChannelHandler<T extends HasRequestId> implements ChannelHandler<T> {

    @Override
    public void handle(String channel, T message) {
        try {
            MDC.put("requestId", message.getRequestId());
            doHandle(channel, message);
        } finally {
            MDC.remove("requestId");
        }
    }

    protected abstract void doHandle(String channel, T message);
}
