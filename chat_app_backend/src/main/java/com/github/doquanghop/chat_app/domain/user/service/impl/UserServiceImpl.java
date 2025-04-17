package com.github.doquanghop.chat_app.domain.user.service.impl;

import com.github.doquanghop.chat_app.domain.account.service.SessionService;
import com.github.doquanghop.chat_app.domain.user.data.dto.request.UpdateUserRequest;
import com.github.doquanghop.chat_app.domain.user.data.dto.response.UserResponse;
import com.github.doquanghop.chat_app.domain.user.data.model.User;
import com.github.doquanghop.chat_app.domain.user.data.repository.UserRepository;
import com.github.doquanghop.chat_app.domain.user.service.UserService;
import com.github.doquanghop.chat_app.infrastructure.model.AppException;
import com.github.doquanghop.chat_app.infrastructure.model.ResourceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final SessionService sessionService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User update(String userId, UpdateUserRequest request) {
        return null;
    }

    @Override
    public UserResponse getUser(String userName) {
        User existingUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ResourceException.ENTITY_NOT_FOUND, "User not found"));
        var sessionStatus = sessionService.getSessionStatus(existingUser.getId());
        return UserResponse.builder()
                .id(existingUser.getId())
                .userName(existingUser.getUserName())
                .fullName(existingUser.getFullName())
                .avatarURL(existingUser.getAvatarURL())
                .isOnline(sessionStatus.isOnline())
                .lastSeen(LocalDateTime.now())
                .build();
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ResourceException.ENTITY_NOT_FOUND, "User not found"));
    }
}