package io.github.dqh999.chat_app.application.component;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Log4j2
public class WebSocketEventListener {
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String userId = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "anonymous";

//        onlineUsers.put(sessionId, userId);
        log.info("User connected - sessionId: {}, userId: {}", sessionId, userId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
//        String userId = onlineUsers.remove(sessionId);

        log.info("User disconnected - sessionId: {}, userId: {}", sessionId, "userId");
    }
}
