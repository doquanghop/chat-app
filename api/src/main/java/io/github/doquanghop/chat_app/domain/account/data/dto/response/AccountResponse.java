package io.github.doquanghop.chat_app.domain.account.data.dto.response;

import io.github.doquanghop.chat_app.domain.account.data.dto.TokenDTO;

public record AccountResponse(
        String id,
        String phoneNumber,
        TokenDTO token
) {
}