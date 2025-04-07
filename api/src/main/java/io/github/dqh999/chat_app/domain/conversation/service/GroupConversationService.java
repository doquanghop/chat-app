package io.github.dqh999.chat_app.domain.conversation.service;

import io.github.dqh999.chat_app.domain.conversation.data.dto.request.CreateConversationRequest;
import io.github.dqh999.chat_app.domain.conversation.data.model.Conversation;

public interface GroupConversationService {
    Conversation create(CreateConversationRequest request);

    void leftConversation(String conversationId);

    void addParticipant(String conversationId, String participantId);

    void removeParticipant(String conversationId, String participantId);
}
