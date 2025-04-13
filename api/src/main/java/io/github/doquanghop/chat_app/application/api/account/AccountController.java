package io.github.doquanghop.chat_app.application.api.account;

import io.github.doquanghop.chat_app.domain.account.data.dto.request.LoginRequest;
import io.github.doquanghop.chat_app.domain.account.data.dto.request.LogoutRequest;
import io.github.doquanghop.chat_app.domain.account.data.dto.request.RefreshTokenRequest;
import io.github.doquanghop.chat_app.domain.account.data.dto.request.RegisterRequest;
import io.github.doquanghop.chat_app.domain.account.data.dto.response.AccountResponse;
import io.github.doquanghop.chat_app.domain.account.service.AccountService;
import io.github.doquanghop.chat_app.infrastructure.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @PostMapping(value = "/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterRequest request) {
        accountService.register(request);
        return ApiResponse.<Void>build().toEntity();
    }

    @PostMapping(value = "/login")
    public ResponseEntity<ApiResponse<AccountResponse>> login(@RequestBody LoginRequest request) {
        log.info("Login request: {}", request.getPhoneNumber());
        var response = accountService.login(request);
        return ApiResponse.<AccountResponse>build().withData(response).toEntity();
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest request) {
        accountService.logout(request);
        return ApiResponse.<Void>build().toEntity();
    }

    @PostMapping(value = "/refreshToken")
    public ResponseEntity<ApiResponse<AccountResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        var response = accountService.refreshToken(request);
        return ApiResponse.<AccountResponse>build().withData(response).toEntity();
    }
}
