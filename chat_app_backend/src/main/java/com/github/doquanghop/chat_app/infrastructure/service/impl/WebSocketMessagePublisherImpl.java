package com.github.doquanghop.chat_app.infrastructure.service.impl;

import com.github.doquanghop.chat_app.infrastructure.constant.QualifierNames;
import com.github.doquanghop.chat_app.infrastructure.service.MessagePublisher;
import com.github.doquanghop.chat_app.infrastructure.model.HasRequestId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service(QualifierNames.WEBSOCKET_MESSAGE_PUBLISHER)
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessagePublisherImpl<T extends HasRequestId> implements MessagePublisher<T> {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void publish(String channel, T message) {
        try {
            messagingTemplate.convertAndSend(channel, message);
            log.info("Published WebSocket message: channel = [{}]", channel);
        } catch (Exception e) {
            log.error("Failed to publish WebSocket message: channel = [{}]", channel, e);
        }
    }
}
