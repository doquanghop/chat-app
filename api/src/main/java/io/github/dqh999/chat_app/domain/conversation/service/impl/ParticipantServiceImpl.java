package io.github.dqh999.chat_app.domain.conversation.service.impl;

import io.github.dqh999.chat_app.domain.conversation.data.model.Participant;
import io.github.dqh999.chat_app.domain.conversation.data.repository.ParticipantRepository;
import io.github.dqh999.chat_app.domain.conversation.service.ParticipantService;
import io.github.dqh999.chat_app.infrastructure.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepository participantRepository;

    @Override
    public Participant updateNickName(String targetAccountId, String conversationId, String nickName) {
        return null;
    }

    @Override
    public void updateLastSeenMessage(String conversationId, String lastSeenMessageId) {

    }

    @Override
    public long getUnreadMessageCount(String conversationId) {
        String currentAccountId = SecurityUtil.getCurrentUserId();
        return participantRepository
                .countUnreadMessagesByAccountIdAndConversationId(currentAccountId, conversationId);
    }
}
