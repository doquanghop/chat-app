package com.github.doquanghop.chat_app.application.api.user;

import com.github.doquanghop.chat_app.domain.user.data.dto.response.UserResponse;
import com.github.doquanghop.chat_app.domain.user.service.UserService;
import com.github.doquanghop.chat_app.infrastructure.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userName}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUserName(@PathVariable String userName) {
        var response = userService.getUser(userName);
        return ApiResponse.<UserResponse>build().withData(response).toEntity();
    }
}
