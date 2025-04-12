package io.github.doquanghop.chat_app.domain.conversation.data.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class CreateConversationGroupRequest {
    String name;
    Set<String> participantIds;
}
