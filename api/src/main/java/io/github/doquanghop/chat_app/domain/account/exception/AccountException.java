package io.github.doquanghop.chat_app.domain.account.exception;

import io.github.doquanghop.chat_app.infrastructure.model.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
public enum AccountException implements ExceptionCode {

    ACCOUNT_INACTIVE(4003, "ACCOUNT_INACTIVE"),
    ACCOUNT_BANNED(4004, "ACCOUNT_BANNED"),
    INVALID_CREDENTIALS(4001, "INVALID_CREDENTIALS"),
    PHONE_NUMBER_NOT_VERIFIED(4005, "PHONE_NUMBER_NOT_VERIFIED"),

    INVALID_TOKEN(4006, "INVALID_TOKEN"),
    TOKEN_BLACKLISTED(4007, "TOKEN_BLACKLISTED"),
    REFRESH_TOKEN_NOT_FOUND(4008, "REFRESH_TOKEN_NOT_FOUND"),
    TOKEN_TYPE_MISMATCH(4009, "TOKEN_TYPE_MISMATCH");

    private final Integer code;
    private final String type;
}
