package com.github.doquanghop.chat_app.domain.account.service;

import com.github.doquanghop.chat_app.domain.account.data.dto.request.LoginRequest;
import com.github.doquanghop.chat_app.domain.account.data.dto.request.LogoutRequest;
import com.github.doquanghop.chat_app.domain.account.data.dto.request.RefreshTokenRequest;
import com.github.doquanghop.chat_app.domain.account.data.dto.request.RegisterRequest;
import com.github.doquanghop.chat_app.domain.account.data.dto.response.AccountResponse;
import com.github.doquanghop.chat_app.infrastructure.model.UserDetail;

public interface AccountService {
    void register(RegisterRequest request);

    AccountResponse login(LoginRequest request);

    void logout(LogoutRequest request);

    UserDetail authenticate(String accessToken);

    AccountResponse refreshToken(RefreshTokenRequest request);

    boolean isValidActiveAccount(String accountId);
}
