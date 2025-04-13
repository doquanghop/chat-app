package io.github.doquanghop.chat_app.domain.conversation.service;

import io.github.doquanghop.chat_app.domain.conversation.data.model.Participant;
import io.github.doquanghop.chat_app.domain.conversation.data.model.ParticipantRole;

import java.util.List;
import java.util.Map;

public interface ParticipantService {
    boolean hasPermission(String conversationId);

    void addParticipants(String conversationId, Map<String, ParticipantRole> participants);

    void addParticipant(String conversationId, String participantId, ParticipantRole role);

    Participant updateNickName(String targetAccountId, String conversationId, String nickName);

    void updateLastSeenMessage(String conversationId, String lastSeenMessageId);

    long getUnreadMessageCount(String conversationId);

    List<String> getParticipantIdsByConversationId(String conversationId);

    Participant getOtherParticipantInPrivateConversation(String conversationId);

    void removeParticipant(String conversationId, String participantId);
}
