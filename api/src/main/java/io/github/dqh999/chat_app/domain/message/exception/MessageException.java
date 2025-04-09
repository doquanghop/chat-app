package io.github.dqh999.chat_app.domain.message.exception;

import io.github.dqh999.chat_app.infrastructure.model.ExceptionCode;
import lombok.Getter;

@Getter
public enum MessageException implements ExceptionCode {
    ;

    @Override
    public Integer getCode() {
        return 0;
    }

    @Override
    public String getType() {
        return "";
    }

}
