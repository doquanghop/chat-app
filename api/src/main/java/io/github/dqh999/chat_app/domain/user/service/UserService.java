package io.github.dqh999.chat_app.domain.user.service;

import io.github.dqh999.chat_app.domain.user.data.model.User;

public interface UserService {
    User getUser(String userName);

    void setOnlineStatus(String userId, boolean isOnline);
}
