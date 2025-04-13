package io.github.doquanghop.chat_app.domain.account.data.dto;

import java.time.LocalDateTime;

public record SessionStatusDTO(
        boolean isOnline,
        LocalDateTime lastSeen
) {
}
