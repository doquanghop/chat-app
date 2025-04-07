package io.github.dqh999.chat_app.domain.account.service;

import jakarta.servlet.http.HttpServletRequest;

public interface AccountSessionService {
    void handleSession(String accountId);
}
