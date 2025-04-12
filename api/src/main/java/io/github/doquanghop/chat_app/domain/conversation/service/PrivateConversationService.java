package io.github.doquanghop.chat_app.domain.conversation.service;

import io.github.doquanghop.chat_app.domain.conversation.data.model.Conversation;

public interface PrivateConversationService extends ConversationService {
    Conversation getPrivateConversationWith(String targetAccountId);
}
