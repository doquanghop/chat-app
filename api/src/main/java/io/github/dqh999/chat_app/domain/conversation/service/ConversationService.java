package io.github.dqh999.chat_app.domain.conversation.service;

import io.github.dqh999.chat_app.domain.conversation.data.model.Conversation;
import io.github.dqh999.chat_app.domain.message.data.model.Message;
import io.github.dqh999.chat_app.infrastructure.util.PageResponse;

public interface ConversationService {
    PageResponse<Conversation> getAllConversations(int page, int size);

    boolean canSendMessage(String conversationId, String senderId);

    void sendNotification(String conversationId, Message message);
}
