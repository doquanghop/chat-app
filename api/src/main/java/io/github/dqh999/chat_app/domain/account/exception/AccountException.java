package io.github.dqh999.chat_app.domain.account.exception;

import io.github.dqh999.chat_app.infrastructure.model.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
public enum AccountException implements ExceptionCode {
    ACCOUNT_INACTIVE(403, "ACCOUNT_INACTIVE", "Account is inactive or disabled."),
    ACCOUNT_BANNED(403, "ACCOUNT_BANNED", "Account has been banned."),
    INVALID_CREDENTIALS(401, "INVALID_CREDENTIALS", "Invalid phone number or password."),
    PHONE_NUMBER_NOT_VERIFIED(403, "PHONE_NUMBER_NOT_VERIFIED", "Phone number has not been verified."),

    INVALID_TOKEN(401, "INVALID_TOKEN", "Token is invalid or expired."),
    TOKEN_BLACKLISTED(401, "TOKEN_BLACKLISTED", "Token has been blacklisted due to logout or security violation."),
    REFRESH_TOKEN_NOT_FOUND(401, "REFRESH_TOKEN_NOT_FOUND", "Refresh token does not exist or has been revoked."),
    TOKEN_TYPE_MISMATCH(401, "TOKEN_TYPE_MISMATCH", "Token type does not match expected type (access/refresh).");

    private final Integer code;
    private final String type;
    private final String message;
}