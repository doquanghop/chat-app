package io.github.doquanghop.chat_app.domain.account.data.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    String phoneNumber;
    String password;
    String userName;
    String fullName;
}
