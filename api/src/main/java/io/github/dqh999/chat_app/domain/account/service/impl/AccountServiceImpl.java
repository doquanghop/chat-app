package io.github.dqh999.chat_app.domain.account.service.impl;

import io.github.dqh999.chat_app.domain.account.component.JwtTokenProvider;
import io.github.dqh999.chat_app.domain.account.data.dto.TokenMetadataDTO;
import io.github.dqh999.chat_app.domain.account.data.dto.request.LoginRequest;
import io.github.dqh999.chat_app.domain.account.data.dto.request.RegisterRequest;
import io.github.dqh999.chat_app.domain.account.data.dto.response.AccountResponse;
import io.github.dqh999.chat_app.domain.account.data.model.Account;
import io.github.dqh999.chat_app.domain.account.data.repository.AccountRepository;
import io.github.dqh999.chat_app.domain.account.service.AccountService;
import io.github.dqh999.chat_app.domain.account.service.AccountSessionService;
import io.github.dqh999.chat_app.domain.user.data.dto.request.UpdateUserRequest;
import io.github.dqh999.chat_app.domain.user.service.UserService;
import io.github.dqh999.chat_app.infrastructure.service.CacheService;
import io.github.dqh999.exception.model.AppException;
import io.github.dqh999.exception.utils.ResourceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserService userService;
    private final CacheService cacheService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountSessionService accountSessionService;
    private static final Duration REGISTER_LOCK_TIMEOUT = Duration.ofSeconds(10);

    @Override
    @Transactional
    public AccountResponse register(RegisterRequest request) {
        String phoneNumber = request.getPhoneNumber();
        String userName = request.getUserName();
        String phoneLockKey = "phoneNumber:lock:" + phoneNumber;
        String userNameLockKey = "userName:lock:" + userName;

        boolean lockedPhoneNumber = cacheService.lock(phoneLockKey, REGISTER_LOCK_TIMEOUT);
        boolean lockedUserName = cacheService.lock(userNameLockKey, REGISTER_LOCK_TIMEOUT);

        try {
            if (!lockedPhoneNumber || accountRepository.existsByPhoneNumber(phoneNumber)) {
                throw new AppException(ResourceException.ENTITY_ALREADY_EXISTS, "PHONE_NUMBER");
            }
            if (!lockedUserName) {
                throw new AppException(ResourceException.ENTITY_ALREADY_EXISTS, "USERNAME");
            }

            String hashPassword = passwordEncoder.encode(request.getPassword());
            Account newAccount = Account.builder()
                    .phoneNumber(phoneNumber)
                    .hashPassword(hashPassword)
                    .createdAt(LocalDateTime.now())
                    .build();
            accountRepository.save(newAccount);

            userService.update(newAccount.getId(), UpdateUserRequest.builder()
                    .userName(userName)
                    .fullName(request.getFullName())
                    .build());
            var token = jwtTokenProvider.generateTokens(new TokenMetadataDTO(newAccount.getId(), userName, new Date()));
            return new AccountResponse(newAccount.getId(), newAccount.getPhoneNumber(), token);
        } finally {
            if (lockedPhoneNumber) {
                cacheService.unlock(phoneLockKey);
            }
            if (lockedUserName) {
                cacheService.unlock(userNameLockKey);
            }
        }
    }

    @Override
    public AccountResponse login(LoginRequest request) {
        Account existingAccount = accountRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new AppException(ResourceException.ENTITY_NOT_FOUND));
        if (!passwordEncoder.matches(request.getPassword(), existingAccount.getHashPassword())) {
            throw new AppException(ResourceException.INVALID_PAYLOAD);
        }
        accountSessionService.handleSession(existingAccount.getId());
        var token = jwtTokenProvider.generateTokens(new TokenMetadataDTO(existingAccount.getId(), "", new Date()));
        return new AccountResponse(existingAccount.getId(), existingAccount.getPhoneNumber(), token);
    }
}
