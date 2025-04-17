package com.github.doquanghop.chat_app.infrastructure.model;

import org.springframework.http.HttpStatus;

public interface ExceptionCode {
    Integer getCode();

    String getType();

    default HttpStatus getHttpStatus() {
        HttpStatus status = HttpStatus.resolve(getCode());
        return status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
