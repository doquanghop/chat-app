package io.github.doquanghop.chat_app.application.fliter;

import io.github.doquanghop.chat_app.domain.account.service.AccountService;
import io.github.doquanghop.chat_app.infrastructure.constant.SecurityConstants;
import io.github.doquanghop.chat_app.infrastructure.model.AppException;
import io.github.doquanghop.chat_app.infrastructure.model.UserDetail;
import io.github.doquanghop.chat_app.infrastructure.utils.ResourceException;
import io.github.doquanghop.chat_app.infrastructure.utils.TokenExtractor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Intercepts WebSocket messages to authenticate users via JWT and store session attributes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final AccountService accountService;

    /**
     * Processes WebSocket messages before they are sent to the channel.
     *
     * @param message The WebSocket message.
     * @param channel The message channel.
     * @return The processed message or null if authentication fails.
     */
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String wsSessionId = accessor.getSessionId();

        log.debug("Processing WebSocket authentication for wsSessionId: {}", wsSessionId);

        try {
            String token = getToken(accessor);
            UserDetail userDetail = accountService.authenticate(token);
            String appSessionId = getAppSessionId(userDetail, wsSessionId);

            Map<String, Object> sessionAttributes = initSessionAttributes(accessor, wsSessionId);
            sessionAttributes.put(SecurityConstants.APP_SESSION_ID_KEY, appSessionId);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            accessor.setUser(authentication);

            log.info("WebSocket authentication successful - wsSessionId: {}, accountId: {}, appSessionId: {}",
                    wsSessionId, userDetail.getId(), appSessionId);

            return message;
        } catch (AppException e) {
            log.error("WebSocket authentication failed - wsSessionId: {}. Error: {}", wsSessionId, e.getMessage());
            throw e;
        }
    }

    /**
     * Extracts the JWT token from the Authorization header.
     *
     * @param accessor The STOMP header accessor.
     * @return The extracted token.
     * @throws AppException If the token is missing.
     */
    private String getToken(StompHeaderAccessor accessor) {
        String token = TokenExtractor.extractToken(accessor.getFirstNativeHeader("Authorization"));
        if (token == null) {
            String wsSessionId = accessor.getSessionId();
            log.warn("Missing Authorization token for wsSessionId: {}", wsSessionId);
            throw new AppException(ResourceException.ACCESS_DENIED, "Missing Authorization token");
        }
        return token;
    }

    /**
     * Retrieves the application session ID from UserDetail.
     *
     * @param userDetail The authenticated user details.
     * @param wsSessionId The WebSocket session ID for logging.
     * @return The application session ID.
     * @throws AppException If the session ID is missing.
     */
    private String getAppSessionId(UserDetail userDetail, String wsSessionId) {
        String appSessionId = userDetail.getSessionId();
        if (appSessionId == null) {
            log.warn("Missing appSessionId for wsSessionId: {}, accountId: {}", wsSessionId, userDetail.getId());
            throw new AppException(ResourceException.ACCESS_DENIED, "Invalid session ID");
        }
        return appSessionId;
    }

    /**
     * Initializes session attributes if not present.
     *
     * @param accessor The STOMP header accessor.
     * @param wsSessionId The WebSocket session ID for logging.
     * @return The initialized session attributes map.
     */
    private Map<String, Object> initSessionAttributes(StompHeaderAccessor accessor, String wsSessionId) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) {
            sessionAttributes = new HashMap<>();
            accessor.setSessionAttributes(sessionAttributes);
            log.debug("Initialized session attributes for wsSessionId: {}", wsSessionId);
        }
        return sessionAttributes;
    }
}