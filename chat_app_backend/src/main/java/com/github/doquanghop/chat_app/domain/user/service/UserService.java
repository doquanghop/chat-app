package com.github.doquanghop.chat_app.domain.user.service;

import com.github.doquanghop.chat_app.domain.user.data.dto.request.UpdateUserRequest;
import com.github.doquanghop.chat_app.domain.user.data.dto.response.UserResponse;
import com.github.doquanghop.chat_app.domain.user.data.model.User;

public interface UserService {
    User update(String userId, UpdateUserRequest request);

    UserResponse getUser(String userName);

    User getUserById(String userId);
}
