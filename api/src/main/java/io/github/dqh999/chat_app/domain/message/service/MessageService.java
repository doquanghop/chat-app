package io.github.dqh999.chat_app.domain.message.service;

import io.github.dqh999.chat_app.domain.message.data.dto.request.SendMessageRequest;
import io.github.dqh999.chat_app.domain.message.data.dto.response.MessageResponse;
import io.github.dqh999.chat_app.domain.message.data.model.Message;
import io.github.dqh999.chat_app.infrastructure.utils.PageResponse;

public interface MessageService {
    MessageResponse send(SendMessageRequest message);

    PageResponse<MessageResponse> getAllMessages(String conversationId, int page, int size);

    PageResponse<MessageResponse> search(String conversationId, String keyword, int page, int size);
}
