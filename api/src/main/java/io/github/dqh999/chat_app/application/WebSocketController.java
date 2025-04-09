package io.github.dqh999.chat_app.application;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
@Controller
@Slf4j
@RequiredArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/send")
    public void handleMessage(String message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId(); // Lấy sessionId của client gửi
        log.info("Received message from session {}: {}", sessionId, message);

        // Gửi lại cho chính user gửi request (theo session)
//        messagingTemplate.convertAndSend("/topic/messages", message);
    }
}