package io.github.dqh999.chat_app.domain.user.service;

import io.github.dqh999.chat_app.domain.user.data.dto.request.UpdateUserRequest;
import io.github.dqh999.chat_app.domain.user.data.model.User;

public interface UserService {
    User update(String userId, UpdateUserRequest request);

    User getUser(String userName);

    User getUserById(String userId);

    void updateOnlineStatus(String userId, boolean isOnline);

}
