package com.github.doquanghop.chat_app.application.api.websocket;

import com.github.doquanghop.chat_app.domain.message.data.dto.SeenEvent;
import com.github.doquanghop.chat_app.domain.message.data.dto.TypingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Slf4j
@Controller
public class WebSocketEventController {

    @MessageMapping("/message/typing")
    @SendTo("/topic/conversation/{conversationId}")
    public TypingEvent handleTyping(TypingEvent event) {
        return event;
    }

    @MessageMapping("/message/seen")
    @SendTo("/topic/conversation/{conversationId}")
    public SeenEvent handleSeen(SeenEvent event) {
        return event;
    }
}