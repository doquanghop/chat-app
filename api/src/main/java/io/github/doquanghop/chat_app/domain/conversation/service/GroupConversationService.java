package io.github.doquanghop.chat_app.domain.conversation.service;

import io.github.doquanghop.chat_app.domain.conversation.data.dto.request.CreateConversationGroupRequest;
import io.github.doquanghop.chat_app.domain.conversation.data.model.Conversation;

public interface GroupConversationService extends ConversationService {
    Conversation create(CreateConversationGroupRequest request);

    void leftConversation(String conversationId);

    void addParticipant(String conversationId, String participantId);

    void removeParticipant(String conversationId, String participantId);
}
