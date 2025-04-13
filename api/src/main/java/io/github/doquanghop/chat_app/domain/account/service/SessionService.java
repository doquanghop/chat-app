package io.github.doquanghop.chat_app.domain.account.service;

import io.github.doquanghop.chat_app.domain.account.data.dto.SessionStatusDTO;

public interface SessionService {
    String createSession(String accountId);

    String getSessionId(String accountId);

    void connectSession(String sessionId);

    void disconnectSession(String sessionId);

    SessionStatusDTO getSessionStatus(String accountId);
}
