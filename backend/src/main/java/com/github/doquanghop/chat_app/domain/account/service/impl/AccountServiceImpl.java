package com.github.doquanghop.chat_app.domain.account.service.impl;

import com.github.doquanghop.chat_app.infrastructure.annotation.logging.ActionLog;
import com.nimbusds.jwt.JWTClaimsSet;
import com.github.doquanghop.chat_app.domain.account.component.JwtTokenProvider;
import com.github.doquanghop.chat_app.domain.account.data.dto.TokenMetadataDTO;
import com.github.doquanghop.chat_app.domain.account.data.dto.request.LoginRequest;
import com.github.doquanghop.chat_app.domain.account.data.dto.request.LogoutRequest;
import com.github.doquanghop.chat_app.domain.account.data.dto.request.RefreshTokenRequest;
import com.github.doquanghop.chat_app.domain.account.data.dto.request.RegisterRequest;
import com.github.doquanghop.chat_app.domain.account.data.dto.response.AccountResponse;
import com.github.doquanghop.chat_app.domain.account.data.model.Account;
import com.github.doquanghop.chat_app.domain.account.data.repository.AccountRepository;
import com.github.doquanghop.chat_app.domain.account.exception.AccountException;
import com.github.doquanghop.chat_app.domain.account.service.AccountService;
import com.github.doquanghop.chat_app.domain.account.service.SessionService;
import com.github.doquanghop.chat_app.infrastructure.model.AppException;
import com.github.doquanghop.chat_app.infrastructure.model.UserDetail;
import com.github.doquanghop.chat_app.infrastructure.service.CacheService;
import com.github.doquanghop.chat_app.infrastructure.model.ResourceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final CacheService cacheService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private static final Duration REGISTER_LOCK_TIMEOUT = Duration.ofSeconds(10);

    @Override
    @Transactional
    @ActionLog(logLevel = "INFO", message = "Registering new account")
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
    @ActionLog(logLevel = "INFO", message = "Logging in account")
    public AccountResponse login(LoginRequest request) {
        Account existingAccount = accountRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new AppException(ResourceException.ENTITY_NOT_FOUND));
        if (!passwordEncoder.matches(request.getPassword(), existingAccount.getHashPassword())) {
            throw new AppException(AccountException.INVALID_CREDENTIALS);
        }
        String sessionId = sessionService.createSession(existingAccount.getId());
        var token = jwtTokenProvider.generateTokens(new TokenMetadataDTO(existingAccount.getId(), List.of("USER"), sessionId, new Date()));
        return new AccountResponse(existingAccount.getId(), existingAccount.getPhoneNumber(), token);
    }

    @Override
    @ActionLog(logLevel = "INFO", message = "Logging out account")
    public void logout(LogoutRequest request) throws AppException {
        if (!jwtTokenProvider.validateToken(request.getAccessToken())) {
            throw new AppException(ResourceException.ACCESS_DENIED);
        }
        Date expirationDate = jwtTokenProvider.getExpirationFromToken(request.getAccessToken());
        Duration ttl = Duration.between(Instant.now(), expirationDate.toInstant());
        cacheService.setBlacklist(request.getAccessToken(), ttl);
    }

    @Override
    @ActionLog(logLevel = "DEBUG", message = "Authenticating user")
    public UserDetail authenticate(String accessToken) throws AppException {
        var claims = jwtTokenProvider.verifyToken(accessToken);
        if (cacheService.isBlacklisted(accessToken)) {
            throw new AppException(ResourceException.ACCESS_DENIED);
        }
        return new UserDetail(claims.getSubject(), jwtTokenProvider.getRolesFromClaims(claims), jwtTokenProvider.getSessionIdFromClaims(claims));
    }

    @Override
    @ActionLog(logLevel = "INFO", message = "Refreshing token")
    public AccountResponse refreshToken(RefreshTokenRequest request) {
        if (cacheService.isBlacklisted(request.getAccessToken())) {
            throw new AppException(AccountException.TOKEN_BLACKLISTED);
        }
        JWTClaimsSet claims = jwtTokenProvider.verifyToken(request.getRefreshToken());
        String accountId = claims.getSubject();
        Date accessTokenExpiration = jwtTokenProvider.getExpirationFromToken(request.getAccessToken());
        if (accessTokenExpiration != null && accessTokenExpiration.after(new Date())) {
            Duration ttl = Duration.between(Instant.now(), accessTokenExpiration.toInstant());
            cacheService.setBlacklist(request.getAccessToken(), ttl);
            log.info("Blacklisted old access token for userId={}", accountId);
        }
        var newTokens = jwtTokenProvider.refreshToken(request.getRefreshToken(), request.getAccessToken());
        return new AccountResponse(accountId, claims.getSubject(), newTokens);
    }

    @Override
    @ActionLog(logLevel = "DEBUG", message = "Checking account validity")
    public boolean isValidActiveAccount(String accountId) {
        return accountRepository.findById(accountId)
                .isPresent();
    }
}
