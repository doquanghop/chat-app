package io.github.dqh999.chat_app.domain.account.data.dto;

import java.util.Date;

public record TokenMetadataDTO(
        String userId,
        String userName,
        Date issuedAt
) {
}
