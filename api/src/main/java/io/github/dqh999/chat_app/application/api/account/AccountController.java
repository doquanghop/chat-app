package io.github.dqh999.chat_app.application.api.account;

import io.github.dqh999.chat_app.domain.account.data.dto.request.LoginRequest;
import io.github.dqh999.chat_app.domain.account.data.dto.request.RegisterRequest;
import io.github.dqh999.chat_app.domain.account.data.dto.response.AccountResponse;
import io.github.dqh999.chat_app.domain.account.service.AccountService;
import io.github.dqh999.chat_app.infrastructure.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping(value = "/register", produces = MediaType.ALL_VALUE)
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterRequest request) {
        accountService.register(request);
        return ApiResponse.<Void>build().toEntity();
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AccountResponse>> login(@RequestBody LoginRequest request) {
        var response = accountService.login(request);
        return ApiResponse.<AccountResponse>build().withData(response).toEntity();
    }
}
