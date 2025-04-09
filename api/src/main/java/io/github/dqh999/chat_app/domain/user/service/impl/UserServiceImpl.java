package io.github.dqh999.chat_app.domain.user.service.impl;

import io.github.dqh999.chat_app.domain.user.data.dto.request.UpdateUserRequest;
import io.github.dqh999.chat_app.domain.user.data.model.User;
import io.github.dqh999.chat_app.domain.user.data.repository.UserRepository;
import io.github.dqh999.chat_app.domain.user.service.UserService;
import io.github.dqh999.chat_app.infrastructure.model.AppException;
import io.github.dqh999.chat_app.infrastructure.utils.ResourceException;
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
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ResourceException.ENTITY_NOT_FOUND, "User not found"));
    }

    @Override
    public void updateOnlineStatus(String userId, boolean isOnline) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ResourceException.ENTITY_NOT_FOUND, "User not found"));

        user.setIsOnline(isOnline);
        if (!isOnline) {
            user.setLastSeen(LocalDateTime.now());
        }

        userRepository.save(user);
    }
}