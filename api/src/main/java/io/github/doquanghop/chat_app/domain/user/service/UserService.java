package io.github.doquanghop.chat_app.domain.user.service;

import io.github.doquanghop.chat_app.domain.user.data.dto.request.UpdateUserRequest;
import io.github.doquanghop.chat_app.domain.user.data.dto.response.UserResponse;
import io.github.doquanghop.chat_app.domain.user.data.model.User;

public interface UserService {
    User update(String userId, UpdateUserRequest request);

    User getUser(String userName);

    UserResponse getUserById(String userId);

    void updateOnlineStatus(String userId, boolean isOnline);

}
