package io.github.dqh999.chat_app.domain.account.service;

import io.github.dqh999.chat_app.domain.account.data.dto.request.LoginRequest;
import io.github.dqh999.chat_app.domain.account.data.dto.request.RegisterRequest;
import io.github.dqh999.chat_app.domain.account.data.dto.response.AccountResponse;

public interface AccountService {
    void register(RegisterRequest request);
    AccountResponse login(LoginRequest request);
}
