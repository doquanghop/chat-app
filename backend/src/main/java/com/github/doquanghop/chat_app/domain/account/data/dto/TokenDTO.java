package com.github.doquanghop.chat_app.domain.account.data.dto;

import java.util.Date;

public record TokenDTO(
        String accountId,
        String accessToken,
        Date accessTokenExpiry,
        String refreshToken,
        Date refreshTokenExpiry
) {
}