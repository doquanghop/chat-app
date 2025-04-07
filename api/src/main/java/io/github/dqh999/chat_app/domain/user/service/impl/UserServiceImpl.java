package io.github.dqh999.chat_app.domain.user.service.impl;

import io.github.dqh999.chat_app.domain.user.data.dto.request.AddUserRequest;
import io.github.dqh999.chat_app.domain.user.data.dto.request.UpdateUserRequest;
import io.github.dqh999.chat_app.domain.user.data.model.User;
import io.github.dqh999.chat_app.domain.user.data.repository.UserRepository;
import io.github.dqh999.chat_app.domain.user.service.UserService;
import io.github.dqh999.exception.model.AppException;
import io.github.dqh999.exception.utils.ResourceException;
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
    public void createUser(AddUserRequest request) {
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new AppException(ResourceException.ENTITY_ALREADY_EXISTS, "USERNAME_ALREADY_EXISTS");
        }
        User user = new User(
                request.getAccountId(),
                request.getUserName(),
                request.getFullName(),
                null,
                true,
                LocalDateTime.now()
        );
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(String userId, UpdateUserRequest request) {
        // Tìm user theo userId
        User user = userRepository.findById(userId)
                .orElseGet(() -> {
                    // Nếu không tồn tại, tạo user mới
                    User newUser = new User();
                    newUser.setId(userId);
                    newUser.setUserName(request.getUserName());
                    newUser.setFullName(request.getFullName());
                    newUser.setAvatarURL(request.getAvatarURL());
                    newUser.setOnline(false); // Mặc định offline khi tạo
                    return newUser;
                });

        // Update userName (nếu có và khác hiện tại)
        if (request.getUserName() != null && !request.getUserName().equals(user.getUserName())) {
            // Check username đã tồn tại chưa
            if (userRepository.existsByUserName(request.getUserName())) {
                throw new AppException(ResourceException.ENTITY_ALREADY_EXISTS, "Username already taken");
            }
            user.setUserName(request.getUserName());
        }

        // Update fullName (nếu có)
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        // Update avatarURL (nếu có)
        if (request.getAvatarURL() != null) {
            user.setAvatarURL(request.getAvatarURL());
        }

        // Lưu vào DB và trả về
        return userRepository.save(user);
    }

    @Override
    public User getUser(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ResourceException.ENTITY_NOT_FOUND, "User not found"));
    }

    @Override
    public void setOnlineStatus(String userId, boolean isOnline) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ResourceException.ENTITY_NOT_FOUND, "User not found"));
        user.setOnline(isOnline);
        user.setLastSeen(isOnline ? null : LocalDateTime.now());
        userRepository.save(user);
    }
}