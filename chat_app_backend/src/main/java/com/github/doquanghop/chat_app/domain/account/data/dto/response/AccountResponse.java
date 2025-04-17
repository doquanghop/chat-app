package com.github.doquanghop.chat_app.domain.account.data.dto.response;

import com.github.doquanghop.chat_app.domain.account.data.dto.TokenDTO;
import com.github.doquanghop.chat_app.infrastructure.annotation.logging.MaskSensitive;

public record AccountResponse(
        String id,
        String phoneNumber,
        @MaskSensitive(maskValue = "abc") TokenDTO token
) {
}