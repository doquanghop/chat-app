package io.github.dqh999.chat_app.domain.user.service;

import io.github.dqh999.chat_app.domain.user.data.dto.request.AddUserRequest;
import io.github.dqh999.chat_app.domain.user.data.dto.request.UpdateUserRequest;
import io.github.dqh999.chat_app.domain.user.data.model.User;

public interface UserService {
    void createUser(AddUserRequest request);

    User update(String userId, UpdateUserRequest request);

    User getUser(String userName);

    void setOnlineStatus(String userId, boolean isOnline);
}
