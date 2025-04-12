package io.github.doquanghop.chat_app.domain.account.service.impl;

import io.github.doquanghop.chat_app.domain.account.data.repository.LoginSessionRepository;
import io.github.doquanghop.chat_app.domain.account.service.LoginSessionTrackerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import io.github.doquanghop.chat_app.infrastructure.utils.HttpRequestUtils;


@Service
@RequiredArgsConstructor
public class LoginSessionTrackerServiceImpl implements LoginSessionTrackerService {
    private final LoginSessionRepository loginSessionRepository;

    @Override
    public void handleSession(String accountId) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest httpServletRequest = attributes.getRequest();

        String ipAddress = HttpRequestUtils.getClientIp(httpServletRequest);
        String userAgentString = httpServletRequest.getHeader("User-Agent");
    }

}
