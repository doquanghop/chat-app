package com.github.doquanghop.chat_app.domain.account.exception;

import com.github.doquanghop.chat_app.infrastructure.model.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
public enum AccountException implements ExceptionCode {
    ACCOUNT_INACTIVE(4003, "ACCOUNT_INACTIVE", HttpStatus.FORBIDDEN), // 403
    ACCOUNT_BANNED(4004, "ACCOUNT_BANNED", HttpStatus.FORBIDDEN), // 403
    INVALID_CREDENTIALS(4001, "INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED), // 401
    PHONE_NUMBER_NOT_VERIFIED(4005, "PHONE_NUMBER_NOT_VERIFIED", HttpStatus.BAD_REQUEST), // 400

    INVALID_TOKEN(4006, "INVALID_TOKEN", HttpStatus.UNAUTHORIZED), // 401
    TOKEN_BLACKLISTED(4007, "TOKEN_BLACKLISTED", HttpStatus.UNAUTHORIZED), // 401
    REFRESH_TOKEN_NOT_FOUND(4008, "REFRESH_TOKEN_NOT_FOUND", HttpStatus.UNAUTHORIZED), // 401
    EXPIRED_TOKEN(4009, "EXPIRED_TOKEN", HttpStatus.UNAUTHORIZED); // 401, sửa code để tránh trùng lặp

    private final Integer code;
    private final String type;
    private final HttpStatus httpStatus;
}
