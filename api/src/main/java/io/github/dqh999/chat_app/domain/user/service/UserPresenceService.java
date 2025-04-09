package io.github.dqh999.chat_app.domain.user.service;

import io.github.dqh999.chat_app.domain.user.data.model.User;

public interface UserPresenceService {
    void registerSession(String sessionId, String userId);

    User getUserBySession(String sessionId);

    void removeSession(String sessionId);

    boolean isSessionActive(String sessionId);
}
