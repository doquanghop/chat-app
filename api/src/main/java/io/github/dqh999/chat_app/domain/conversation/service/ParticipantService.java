package io.github.dqh999.chat_app.domain.conversation.service;

import io.github.dqh999.chat_app.domain.conversation.data.model.Participant;

public interface ParticipantService {
    Participant updateNickName(String targetAccountId, String conversationId, String nickName);

    void updateLastSeenMessage(String conversationId, String lastSeenMessageId);

    long getUnreadMessageCount(String conversationId);
}
