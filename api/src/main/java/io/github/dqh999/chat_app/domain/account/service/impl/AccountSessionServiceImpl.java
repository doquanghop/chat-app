package io.github.dqh999.chat_app.domain.account.service.impl;

import io.github.dqh999.chat_app.domain.account.data.model.AccountSession;
import io.github.dqh999.chat_app.domain.account.data.repository.AccountSessionRepository;
import io.github.dqh999.chat_app.domain.account.service.AccountSessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountSessionServiceImpl implements AccountSessionService {
    private final AccountSessionRepository accountSessionRepository;

    @Override
    public void handleSession(String accountId) {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ipAddress = httpServletRequest.getRemoteAddr();
        String userAgent = httpServletRequest.getHeader("User-Agent");
        AccountSession session = AccountSession.builder()
                .accountId(accountId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .loginAt(LocalDateTime.now())
                .build();
        accountSessionRepository.save(session);
    }
}
