package io.github.dqh999.chat_app.application.fliter;

import io.github.dqh999.chat_app.domain.account.service.AccountService;
import io.github.dqh999.chat_app.infrastructure.model.AppException;
import io.github.dqh999.chat_app.infrastructure.model.UserDetail;
import io.github.dqh999.chat_app.infrastructure.utils.ResourceException;
import io.github.dqh999.chat_app.infrastructure.utils.TokenExtractor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final AccountService accountService;

    @Override
    public Message<?> preSend(
            @NonNull Message<?> message,
            @NonNull MessageChannel channel
    ) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = TokenExtractor.extractToken(accessor.getFirstNativeHeader("Authorization"));
            if (token == null) {
                throw new AppException(ResourceException.ACCESS_DENIED);
            }
            UserDetail userDetail = accountService.authenticate(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            accessor.setUser(authentication);
        }
        return message;
    }
}
