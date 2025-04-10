package io.github.dqh999.chat_app.domain.user.data.dto.response;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Data
public class UserResponse {
    String id;
    String userName;
    String fullName;
    String avatarURL;
    boolean isOnline;
}
