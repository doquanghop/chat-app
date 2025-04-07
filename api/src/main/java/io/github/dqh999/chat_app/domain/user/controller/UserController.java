package io.github.dqh999.chat_app.domain.user.controller;

import io.github.dqh999.chat_app.domain.user.data.model.User;
import io.github.dqh999.chat_app.domain.user.service.UserService;
import io.github.dqh999.exception.model.ApiResponse;
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
    public ResponseEntity<ApiResponse<User>> getUserByUserName(@PathVariable String userName) {
        var response = userService.getUser(userName);
        return ApiResponse.<User>build().withData(response).toEntity();
    }
}
