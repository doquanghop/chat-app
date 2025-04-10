package io.github.dqh999.chat_app.domain.conversation.service;

import io.github.dqh999.chat_app.domain.conversation.data.dto.response.ConversationResponse;
import io.github.dqh999.chat_app.domain.conversation.data.model.Conversation;
import io.github.dqh999.chat_app.domain.message.data.model.Message;
import io.github.dqh999.chat_app.infrastructure.utils.PageResponse;

public interface ConversationService {
    PageResponse<ConversationResponse> getAllConversations(int page, int size);

    void checkSenderPermission(String conversationId, String senderId);

    void sendNotification(String conversationId, Message message);
}
