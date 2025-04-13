package io.github.doquanghop.chat_app.domain.account.service.impl;

import io.github.doquanghop.chat_app.domain.account.data.dto.SessionStatusDTO;
import io.github.doquanghop.chat_app.domain.account.data.model.Session;
import io.github.doquanghop.chat_app.domain.account.data.repository.SessionRepository;
import io.github.doquanghop.chat_app.domain.account.service.SessionService;
import io.github.doquanghop.chat_app.infrastructure.model.AppException;
import io.github.doquanghop.chat_app.infrastructure.utils.DeviceIdUtils;
import io.github.doquanghop.chat_app.infrastructure.utils.ResourceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private static final String UNKNOWN_VALUE = "unknown";
    private static final String USER_AGENT_HEADER = "User-Agent";

    private final SessionRepository sessionRepository;

    @Override
    public String createSession(String accountId) {
        HttpServletRequest httpRequest = getHttpServletRequest();
        String userAgent = extractUserAgent(httpRequest);
        String ipAddress = extractIpAddress(httpRequest);
        String deviceId = DeviceIdUtils.generateDeviceId(accountId, userAgent);

        log.debug("Creating session for accountId: {}, deviceId: {}, ipAddress: {}, userAgent: {}",
                accountId, deviceId, ipAddress, userAgent);

        Session session = sessionRepository.findByDeviceId(deviceId)
                .orElseGet(() -> Session.builder()
                        .id(UUID.randomUUID().toString())
                        .accountId(accountId)
                        .deviceId(deviceId)
                        .ipAddress(ipAddress)
                        .userAgent(userAgent)
                        .build());

        session.setLoginAt(LocalDateTime.now());
        session.setIsOnline(true);
        session = sessionRepository.save(session);

        log.info("Created session - sessionId: {}, accountId: {}, deviceId: {}",
                session.getId(), accountId, deviceId);
        return session.getId();
    }

    @Override
    public String getSessionId(String accountId) {
        HttpServletRequest httpRequest = getHttpServletRequest();
        String userAgent = extractUserAgent(httpRequest);
        String deviceId = DeviceIdUtils.generateDeviceId(accountId, userAgent);

        log.debug("Retrieving session for accountId: {}, deviceId: {}", accountId, deviceId);

        Session session = sessionRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> {
                    log.warn("No session found for accountId: {}, deviceId: {}", accountId, deviceId);
                    return new AppException(ResourceException.ACCESS_DENIED, "Session not found");
                });

        log.debug("Found session - sessionId: {}, accountId: {}, deviceId: {}",
                session.getId(), accountId, deviceId);
        return session.getId();
    }

    @Override
    public void connectSession(String sessionId) {
        log.debug("Connecting WebSocket - sessionId: {}", sessionId);

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    log.warn("No session found for sessionId: {}", sessionId);
                    return new AppException(ResourceException.ENTITY_NOT_FOUND, "Session not found");
                });

        session.setIsOnline(true);
        session.setLastSeen(LocalDateTime.now());
        sessionRepository.save(session);

        log.info("WebSocket connected - sessionId: {}, accountId: {}, deviceId: {}",
                sessionId, session.getAccountId(), session.getDeviceId());
    }

    @Override
    public void disconnectSession(String sessionId) {
        log.debug("Disconnecting WebSocket - wsSessionId: {}", sessionId);

        Optional<Session> optionalSession = sessionRepository.findById(sessionId);
        if (optionalSession.isEmpty()) {
            log.warn("No session found for wsSessionId: {}", sessionId);
            return;
        }

        Session session = optionalSession.get();
        session.setIsOnline(false);
        session.setLastSeen(LocalDateTime.now());
        sessionRepository.save(session);

        log.info("WebSocket disconnected - sessionId: {}, accountId: {}, deviceId: {}",
                sessionId, session.getAccountId(), session.getDeviceId());
    }

    @Override
    public SessionStatusDTO getSessionStatus(String accountId) {
        if (sessionRepository.existsByAccountIdAndIsOnlineTrue(accountId)) {
            return new SessionStatusDTO(true, LocalDateTime.now());
        }
        LocalDateTime lastSeen = sessionRepository.findLatestLastSeenByAccountId(accountId)
                .orElse(LocalDateTime.now());
        return new SessionStatusDTO(false, lastSeen);
    }

    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.error("HTTP request context unavailable");
            throw new AppException(ResourceException.INTERNAL_SERVER_ERROR, "Request context unavailable");
        }
        return attributes.getRequest();
    }

    private String extractUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader(USER_AGENT_HEADER);
        String result = Objects.requireNonNullElse(userAgent, UNKNOWN_VALUE);
        log.debug("Extracted User-Agent: {}", result);
        return result;
    }

    private String extractIpAddress(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            ipAddress = forwardedFor.split(",")[0].trim();
        }
        String result = Objects.requireNonNullElse(ipAddress, UNKNOWN_VALUE);
        log.debug("Extracted IP address: {}", result);
        return result;
    }
}