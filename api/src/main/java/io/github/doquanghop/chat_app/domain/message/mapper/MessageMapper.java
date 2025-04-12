package io.github.doquanghop.chat_app.domain.message.mapper;

import io.github.doquanghop.chat_app.domain.message.data.dto.response.MessageResponse;
import io.github.doquanghop.chat_app.domain.message.data.model.Message;
import io.github.doquanghop.chat_app.domain.user.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    private final UserService userService;

    public MessageMapper(UserService userService) {
        this.userService = userService;
    }

    public MessageResponse toMessageResponse(Message message) {
        var sender = userService.getUserById(message.getSenderId());
        return MessageResponse.builder()
                .id(message.getId())
                .sender(sender)
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}