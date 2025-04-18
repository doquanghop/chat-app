package com.github.doquanghop.chat_app.domain.account.data.dto.request;

import com.github.doquanghop.chat_app.infrastructure.annotation.logging.MaskSensitive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    String phoneNumber;
    @MaskSensitive
    String password;
}
