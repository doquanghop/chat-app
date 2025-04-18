package com.github.doquanghop.chat_app.domain.user.data.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
public class AddUserRequest {
    String userName;
    String fullName;
}
