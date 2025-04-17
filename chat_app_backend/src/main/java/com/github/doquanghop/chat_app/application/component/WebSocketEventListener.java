package com.github.doquanghop.chat_app.application.component;

import com.github.doquanghop.chat_app.domain.account.service.SessionService;
import com.github.doquanghop.chat_app.infrastructure.constant.SecurityConstants;
import com.github.doquanghop.chat_app.infrastructure.model.AppException;
import com.github.doquanghop.chat_app.infrastructure.model.ResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SessionService sessionService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String wsSessionId = accessor.getSessionId();

        log.debug("Processing WebSocket connection for wsSessionId: {}", wsSessionId);

        try {
            String appSessionId = getAppSessionId(accessor);
            sessionService.connectSession(appSessionId);
            log.info("Connected WebSocket - wsSessionId: {}, appSessionId: {}", wsSessionId, appSessionId);
        } catch (AppException e) {
            log.error("Failed to connect WebSocket - wsSessionId: {}. Error: {}", wsSessionId, e.getMessage());
            throw e;
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String wsSessionId = accessor.getSessionId();
        log.info("Processing WebSocket disconnection for wsSessionId: {}", wsSessionId);
        try {
            String appSessionId = getAppSessionId(accessor);
            sessionService.disconnectSession(appSessionId);
            log.info("Disconnected WebSocket - wsSessionId: {}, appSessionId: {}", wsSessionId, appSessionId);
        } catch (AppException e) {
            log.error("Failed to disconnect WebSocket - wsSessionId: {}. Error: {}", wsSessionId, e.getMessage());
            throw e;
        }
    }


    private String getAppSessionId(StompHeaderAccessor accessor) throws NullPointerException {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) {
            log.warn("Missing session attributes for wsSessionId: {}", accessor.getSessionId());
            throw new AppException(ResourceException.ACCESS_DENIED, "Missing session attributes");
        }
        String appSessionId = sessionAttributes.get(SecurityConstants.APP_SESSION_ID_KEY).toString();
        if (appSessionId == null) {
            log.warn("Missing appSessionId for wsSessionId: {}", accessor.getSessionId());
            throw new AppException(ResourceException.ACCESS_DENIED, "Missing session ID");
        }
        return appSessionId;
    }
}