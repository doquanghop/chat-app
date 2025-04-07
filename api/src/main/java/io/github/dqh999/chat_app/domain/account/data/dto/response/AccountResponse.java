package io.github.dqh999.chat_app.domain.account.data.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountResponse {
    String id;
    String phoneNumber;
    String userName;
    String accessToken;
}
