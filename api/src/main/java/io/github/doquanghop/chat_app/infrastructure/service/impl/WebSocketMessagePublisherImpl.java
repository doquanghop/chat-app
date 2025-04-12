package io.github.doquanghop.chat_app.infrastructure.service.impl;

import io.github.doquanghop.chat_app.infrastructure.constant.QualifierNames;
import io.github.doquanghop.chat_app.infrastructure.service.MessagePublisher;
import io.github.doquanghop.chat_app.infrastructure.model.HasRequestId;
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
            log.info("Published WebSocket message: channel = [{}], requestId = [{}]",
                    channel, message.getRequestId());
        } catch (Exception e) {
            log.error("Failed to publish WebSocket message: channel = [{}], requestId = [{}]",
                    channel, message.getRequestId(), e);
        }
    }
}
