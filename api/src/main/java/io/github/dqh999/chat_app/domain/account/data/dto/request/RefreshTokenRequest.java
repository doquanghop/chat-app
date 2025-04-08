package io.github.dqh999.chat_app.domain.account.data.dto.request;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String accessToken;
    private String refreshToken;
}
