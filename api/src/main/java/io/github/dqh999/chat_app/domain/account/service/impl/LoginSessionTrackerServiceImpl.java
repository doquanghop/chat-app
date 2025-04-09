package io.github.dqh999.chat_app.domain.account.service.impl;

import io.github.dqh999.chat_app.domain.account.data.repository.LoginSessionRepository;
import io.github.dqh999.chat_app.domain.account.service.LoginSessionTrackerService;
import io.github.dqh999.chat_app.infrastructure.utils.DeviceUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import io.github.dqh999.chat_app.infrastructure.utils.HttpRequestUtils;


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

//        String deviceId = DeviceUtils.generateDeviceId(ipAddress, userAgentString, browser, operatingSystem, deviceType);
//        Optional<LoginSession> optionalSession = loginSessionRepository
//                .findByAccountIdAndDeviceId(accountId, deviceId);
//
//        optionalSession.ifPresentOrElse(existingSession -> {
//            existingSession.setLoginAt(LocalDateTime.now());
////            existingSession.setLastSeen(LocalDateTime.now());
////            existingSession.setIsOnline(true);
//            loginSessionRepository.save(existingSession);
//        }, () -> {
//            LoginSession newSession = LoginSession.builder()
////                    .sessionId(generateSessionId())
//                    .accountId(accountId)
//                    .ipAddress(ipAddress)
//                    .deviceId(deviceId)
//                    .userAgent(userAgentString)
//                    .browser(browser)
//                    .operatingSystem(operatingSystem)
//                    .deviceType(deviceType)
//                    .loginAt(LocalDateTime.now())
////                    .lastSeen(LocalDateTime.now())
//                    .isActive(true)
////                    .isOnline(true)
//                    .build();
//            loginSessionRepository.save(newSession);
//        });
    }

}
