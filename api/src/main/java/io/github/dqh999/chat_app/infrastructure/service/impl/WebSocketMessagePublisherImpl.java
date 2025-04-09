package io.github.dqh999.chat_app.infrastructure.service.impl;

import io.github.dqh999.chat_app.infrastructure.constant.QualifierNames;
import io.github.dqh999.chat_app.infrastructure.service.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service(QualifierNames.WEBSOCKET_MESSAGE_PUBLISHER)
@RequiredArgsConstructor
public class WebSocketMessagePublisherImpl implements MessagePublisher {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void publish(String channel, Object message) {
        messagingTemplate.convertAndSend(channel, message);
    }
}
