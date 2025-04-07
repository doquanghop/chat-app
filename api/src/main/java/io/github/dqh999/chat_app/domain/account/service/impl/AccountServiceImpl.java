package io.github.dqh999.chat_app.domain.account.service.impl;

import io.github.dqh999.chat_app.domain.account.data.dto.request.LoginRequest;
import io.github.dqh999.chat_app.domain.account.data.dto.request.RegisterRequest;
import io.github.dqh999.chat_app.domain.account.data.dto.response.AccountResponse;
import io.github.dqh999.chat_app.domain.account.data.model.Account;
import io.github.dqh999.chat_app.domain.account.data.repository.AccountRepository;
import io.github.dqh999.chat_app.domain.account.service.AccountService;
import io.github.dqh999.exception.model.AppException;
import io.github.dqh999.exception.utils.ResourceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl
        implements AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AccountResponse register(RegisterRequest request) {
        if (accountRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ResourceException.ENTITY_ALREADY_EXISTS);
        }
        String hashPassword = passwordEncoder.encode(request.getPassword());
        Account account = Account.builder()
                .phoneNumber(request.getPhoneNumber())
                .hashPassword(hashPassword)
                .createdAt(LocalDateTime.now())
                .build();
        accountRepository.save(account);
        return null;
    }

    @Override
    public AccountResponse login(LoginRequest request) {
        Account account = accountRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new AppException(ResourceException.ENTITY_NOT_FOUND));
        if (!passwordEncoder.matches(request.getPassword(), account.getHashPassword())) {
            throw new AppException(ResourceException.INVALID_PAYLOAD);
        }
        return null;
    }
}
