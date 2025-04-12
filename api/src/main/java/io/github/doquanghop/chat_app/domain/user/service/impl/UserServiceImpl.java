package io.github.doquanghop.chat_app.domain.user.service.impl;

import io.github.doquanghop.chat_app.domain.user.data.dto.request.UpdateUserRequest;
import io.github.doquanghop.chat_app.domain.user.data.dto.response.UserResponse;
import io.github.doquanghop.chat_app.domain.user.data.model.User;
import io.github.doquanghop.chat_app.domain.user.data.repository.UserRepository;
import io.github.doquanghop.chat_app.domain.user.service.UserService;
import io.github.doquanghop.chat_app.infrastructure.model.AppException;
import io.github.doquanghop.chat_app.infrastructure.utils.ResourceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User update(String userId, UpdateUserRequest request) {
        return null;
    }

    @Override
    public User getUser(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ResourceException.ENTITY_NOT_FOUND, "User not found"));
    }

    @Override
    public UserResponse getUserById(String userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ResourceException.ENTITY_NOT_FOUND, "User not found"));
        return UserResponse.builder()
                .id(existingUser.getId())
                .userName(existingUser.getUserName())
                .fullName(existingUser.getFullName())
                .avatarURL(existingUser.getAvatarURL())
                .isOnline(true)
                .build();
    }
}