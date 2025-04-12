package io.github.doquanghop.chat_app.infrastructure.utils;

import io.github.doquanghop.chat_app.infrastructure.model.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
public enum ResourceException implements ExceptionCode {
    ENTITY_NOT_FOUND(4004, "ENTITY_NOT_FOUND"),
    ENTITY_ALREADY_EXISTS(4009, "ENTITY_ALREADY_EXISTS"),
    INVALID_ENTITY_ID(4000, "INVALID_ENTITY_ID"),
    CREATION_FAILED(5000, "CREATION_FAILED"),
    UPDATE_FAILED(5001, "UPDATE_FAILED"),
    DELETE_FAILED(5002, "DELETE_FAILED"),
    ACCESS_DENIED(4003, "ACCESS_DENIED"),
    INVALID_PAYLOAD(4001, "INVALID_PAYLOAD"),
    UNEXPECTED_ERROR(5999, "UNEXPECTED_ERROR");

    private final Integer code;
    private final String type;
}
