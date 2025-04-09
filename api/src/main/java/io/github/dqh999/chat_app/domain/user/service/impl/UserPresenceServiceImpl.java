package io.github.dqh999.chat_app.domain.user.service.impl;

import io.github.dqh999.chat_app.domain.user.data.model.User;
import io.github.dqh999.chat_app.domain.user.service.UserService;
import io.github.dqh999.chat_app.domain.user.service.UserPresenceService;
import io.github.dqh999.chat_app.infrastructure.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserPresenceServiceImpl implements UserPresenceService {
    private final UserService userService;
    private final CacheService cacheService;

    private static final String USER_SESSIONS_KEY_PREFIX = "user:sessions:";
    private static final String SESSION_USER_KEY_PREFIX = "session:user:";

    @Override
    public void registerSession(String sessionId, String userId) {
        // Add session to user's session set
        String userSessionsKey = USER_SESSIONS_KEY_PREFIX + userId;
        Set<String> sessions = cacheService.<Set<String>>get(userSessionsKey)
                .orElse(new HashSet<>());
        sessions.add(sessionId);
        cacheService.set(userSessionsKey, sessions);

        // Map session to user
        String sessionUserKey = SESSION_USER_KEY_PREFIX + sessionId;
        cacheService.set(sessionUserKey, userId);

        // Update status
        userService.updateOnlineStatus(userId, true);
    }

    @Override
    public User getUserBySession(String sessionId) {
        String sessionUserKey = SESSION_USER_KEY_PREFIX + sessionId;
        Optional<String> userIdOpt = cacheService.get(sessionUserKey);
        return userIdOpt.map(userService::getUserById).orElse(null);
    }

    @Override
    public void removeSession(String sessionId) {
        String sessionUserKey = SESSION_USER_KEY_PREFIX + sessionId;
        Optional<String> userIdOpt = cacheService.get(sessionUserKey);
        if (userIdOpt.isEmpty()) return;

        String userId = userIdOpt.get();
        String userSessionsKey = USER_SESSIONS_KEY_PREFIX + userId;

        Set<String> sessions = cacheService.<Set<String>>get(userSessionsKey).orElse(new HashSet<>());
        sessions.remove(sessionId);
        if (sessions.isEmpty()) {
            cacheService.delete(userSessionsKey);
            userService.updateOnlineStatus(userId, false);
        } else {
            cacheService.set(userSessionsKey, sessions);
        }

        cacheService.delete(sessionUserKey);
    }

    @Override
    public boolean isSessionActive(String sessionId) {
        String sessionUserKey = SESSION_USER_KEY_PREFIX + sessionId;
        return cacheService.exists(sessionUserKey);
    }
}
