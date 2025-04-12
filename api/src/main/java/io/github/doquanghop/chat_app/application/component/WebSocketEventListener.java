package io.github.doquanghop.chat_app.application.component;

import io.github.doquanghop.chat_app.infrastructure.model.UserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Log4j2
@RequiredArgsConstructor
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        UserInfo userInfo = extractUserInfo(headerAccessor);
        log.info("User connected - sessionId: {}, userId: {}, username: {}",
                sessionId, userInfo.userId, userInfo.username);

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        String userId = "anonymous";
        String username = "anonymous";


        log.info("User disconnected - sessionId: {}, userId: {}, username: {}",
                sessionId, userId, username);
    }

    private UserInfo extractUserInfo(StompHeaderAccessor headerAccessor) {
        String userId = "anonymous";
        String username = "anonymous";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetail userDetail) {
            userId = userDetail.getId();
            username = userDetail.getUsername();
            log.info("Extracted from SecurityContext - userId: {}, username: {}", userId, username);
        } else {
            log.warn("No authenticated user found in SecurityContext");
        }

        return new UserInfo(userId, username);
    }

    private record UserInfo(String userId, String username) {
    }
}