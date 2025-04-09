package io.github.dqh999.chat_app.infrastructure.util;


import io.github.dqh999.chat_app.infrastructure.model.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
public enum ResourceException implements ExceptionCode {
    ENTITY_NOT_FOUND(404, "ENTITY_NOT_FOUND", "Entity not found."),
    ENTITY_ALREADY_EXISTS(409, "ENTITY_ALREADY_EXISTS", "Entity already exists."),
    INVALID_ENTITY_ID(400, "INVALID_ENTITY_ID", "Invalid entity ID."),
    CREATION_FAILED(500, "CREATION_FAILED", "Failed to create entity."),
    UPDATE_FAILED(500, "UPDATE_FAILED", "Failed to update entity."),
    DELETE_FAILED(500, "DELETE_FAILED", "Failed to delete entity."),
    ACCESS_DENIED(403, "ACCESS_DENIED", "Access is denied."),
    INVALID_PAYLOAD(400, "INVALID_PAYLOAD", "Invalid data provided."),
    UNEXPECTED_ERROR(500, "UNEXPECTED_ERROR", "Unexpected error occurred.");


    private final Integer code;
    private final String type;
    private final String message;
}