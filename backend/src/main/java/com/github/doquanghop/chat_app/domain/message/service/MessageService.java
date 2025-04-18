package com.github.doquanghop.chat_app.domain.message.service;

import com.github.doquanghop.chat_app.domain.message.data.dto.request.GetMessageRequest;
import com.github.doquanghop.chat_app.domain.message.data.dto.request.SendMessageRequest;
import com.github.doquanghop.chat_app.domain.message.data.dto.response.MessageResponse;
import com.github.doquanghop.chat_app.infrastructure.model.PageResponse;

public interface MessageService {
    MessageResponse send(String conversationId, SendMessageRequest message);

    PageResponse<MessageResponse> getAllMessages(GetMessageRequest request);
}
