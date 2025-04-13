package io.github.doquanghop.chat_app.domain.user.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    String id;
    String userName;
    String fullName;
    String avatarURL;
    boolean isOnline;
    LocalDateTime lastSeen;
}
