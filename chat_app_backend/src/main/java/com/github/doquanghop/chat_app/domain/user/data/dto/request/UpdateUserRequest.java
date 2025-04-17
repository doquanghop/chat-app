package com.github.doquanghop.chat_app.domain.user.data.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class UpdateUserRequest {
    String userName;
    String fullName;
    String avatarURL;
}
