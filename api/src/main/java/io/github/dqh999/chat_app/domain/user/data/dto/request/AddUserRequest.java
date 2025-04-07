package io.github.dqh999.chat_app.domain.user.data.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class AddUserRequest {
    String accountId;
    String userName;
    String fullName;
}
