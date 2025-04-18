package com.github.doquanghop.chat_app.domain.account.data.dto.request;

import com.github.doquanghop.chat_app.infrastructure.annotation.logging.MaskSensitive;
import lombok.Data;

@Data
public class LogoutRequest {
    private @MaskSensitive String accessToken;
}
