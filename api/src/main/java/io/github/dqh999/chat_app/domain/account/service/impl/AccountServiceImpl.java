package io.github.dqh999.chat_app.domain.account.service.impl;

import io.github.dqh999.chat_app.domain.account.component.JwtTokenProvider;
import io.github.dqh999.chat_app.domain.account.data.dto.TokenMetadataDTO;
import io.github.dqh999.chat_app.domain.account.data.dto.request.LoginRequest;
import io.github.dqh999.chat_app.domain.account.data.dto.request.LogoutRequest;
import io.github.dqh999.chat_app.domain.account.data.dto.request.RefreshTokenRequest;
import io.github.dqh999.chat_app.domain.account.data.dto.request.RegisterRequest;
import io.github.dqh999.chat_app.domain.account.data.dto.response.AccountResponse;
import io.github.dqh999.chat_app.domain.account.data.model.Account;
import io.github.dqh999.chat_app.domain.account.data.repository.AccountRepository;
import io.github.dqh999.chat_app.domain.account.exception.AccountException;
import io.github.dqh999.chat_app.domain.account.service.AccountService;
import io.github.dqh999.chat_app.domain.account.service.AccountSessionService;
import io.github.dqh999.chat_app.infrastructure.model.AppException;
import io.github.dqh999.chat_app.infrastructure.model.UserDetail;
import io.github.dqh999.chat_app.infrastructure.service.CacheService;
import io.github.dqh999.chat_app.infrastructure.util.ResourceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final CacheService cacheService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountSessionService accountSessionService;
    private static final Duration REGISTER_LOCK_TIMEOUT = Duration.ofSeconds(10);

    @Override
    @Transactional
    public void register(RegisterRequest request) {
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
            if (!lockedUserName || accountRepository.existsByUserName(userName)) {
                throw new AppException(ResourceException.ENTITY_ALREADY_EXISTS, "USERNAME");
            }

            String hashPassword = passwordEncoder.encode(request.getPassword());
            Account newAccount = Account.builder()
                    .fullName(request.getFullName())
                    .userName(userName)
                    .phoneNumber(phoneNumber)
                    .hashPassword(hashPassword)
                    .createdAt(LocalDateTime.now())
                    .build();
            accountRepository.save(newAccount);
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
            throw new AppException(AccountException.INVALID_CREDENTIALS);
        }
        accountSessionService.handleSession(existingAccount.getId());
        var token = jwtTokenProvider.generateTokens(new TokenMetadataDTO(existingAccount.getId(), "", new Date()));
        return new AccountResponse(existingAccount.getId(), existingAccount.getPhoneNumber(), token);
    }

    @Override
    public void logout(LogoutRequest request) {
        if (!jwtTokenProvider.validateToken(request.getAccessToken())) {
            throw new AppException(ResourceException.ACCESS_DENIED);
        }
        Date expirationDate = jwtTokenProvider.getExpirationFromToken(request.getAccessToken());
        Duration ttl = Duration.between(Instant.now(), expirationDate.toInstant());
        cacheService.setBlacklist(request.getAccessToken(), ttl);
    }

    @Override
    public UserDetail authenticate(String accessToken) {
        var optToken = jwtTokenProvider.verifyToken(accessToken);
        if (optToken.isEmpty() || cacheService.isBlacklisted(accessToken)) {
            throw new AppException(ResourceException.ACCESS_DENIED);
        }
        var claims = optToken.get().getClaims();
//        return new UserDetail(claims.);
        return null;
    }

    @Override
    public AccountResponse refreshToken(RefreshTokenRequest request) {
        return null;
    }
}
