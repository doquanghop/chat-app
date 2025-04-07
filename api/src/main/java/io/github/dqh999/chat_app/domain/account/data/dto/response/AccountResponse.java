package io.github.dqh999.chat_app.domain.account.data.dto.response;

import io.github.dqh999.chat_app.domain.account.data.dto.TokenDTO;

public record AccountResponse(
        String id,
        String phoneNumber,
        TokenDTO token
) {
}