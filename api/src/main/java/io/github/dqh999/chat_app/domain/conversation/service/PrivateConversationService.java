package io.github.dqh999.chat_app.domain.conversation.service;

import io.github.dqh999.chat_app.domain.conversation.data.model.Conversation;

public interface PrivateConversationService  {
    Conversation getPrivateConversationWith(String targetAccountId);
}
