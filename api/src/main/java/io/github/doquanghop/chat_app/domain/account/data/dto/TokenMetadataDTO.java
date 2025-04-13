package io.github.doquanghop.chat_app.domain.account.data.dto;

import java.util.Date;
import java.util.List;

public record TokenMetadataDTO(
        String userId,
        List<String> roles,
        String sessionId,
        Date issuedAt
) {
}
