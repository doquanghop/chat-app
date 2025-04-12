package io.github.doquanghop.chat_app.application.fliter;

import io.github.doquanghop.chat_app.domain.account.service.AccountService;
import io.github.doquanghop.chat_app.infrastructure.model.UserDetail;
import io.github.doquanghop.chat_app.infrastructure.utils.TokenExtractor;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter implements Ordered {
    private final AccountService accountService;
    
    private static final int ORDER = 100;

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("Authenticating token from [{} {}] | IP={} | UA={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );
        String token = TokenExtractor.extractToken(request.getHeader("Authorization"));
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetail userDetail = accountService.authenticate(token);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetail, null, userDetail.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                log.info("Token authenticated successfully for userId={} | username={}",
                        userDetail.getId(), userDetail.getUsername());
            } catch (Exception e) {
                log.error("Failed to authenticate token", e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
