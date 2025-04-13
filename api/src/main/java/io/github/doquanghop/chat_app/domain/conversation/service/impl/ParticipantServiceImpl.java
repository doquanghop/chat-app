package io.github.doquanghop.chat_app.domain.conversation.service.impl;

import io.github.doquanghop.chat_app.domain.account.service.AccountService;
import io.github.doquanghop.chat_app.domain.conversation.data.model.Participant;
import io.github.doquanghop.chat_app.domain.conversation.data.model.ParticipantRole;
import io.github.doquanghop.chat_app.domain.conversation.data.repository.ParticipantRepository;
import io.github.doquanghop.chat_app.domain.conversation.service.ParticipantService;
import io.github.doquanghop.chat_app.infrastructure.model.AppException;
import io.github.doquanghop.chat_app.infrastructure.security.SecurityUtil;
import io.github.doquanghop.chat_app.infrastructure.utils.ResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final AccountService accountService;
    private final ParticipantRepository participantRepository;

    @Override
    public boolean hasPermission(String conversationId) {
        return participantRepository.existsByConversationIdAndAccountId(conversationId, SecurityUtil.getCurrentUserId());
    }

    @Override
    public void addParticipants(String conversationId, Map<String, ParticipantRole> participants) {
        if (participants == null || participants.isEmpty()) {
            throw new AppException(ResourceException.INVALID_PAYLOAD, "Participants list cannot be empty");
        }

        List<Participant> participantList = participants.entrySet().stream()
                .filter(entry -> accountService.isValidActiveAccount(entry.getKey()))
                .map(entry -> Participant.builder()
                        .conversationId(conversationId)
                        .accountId(entry.getKey())
                        .role(entry.getValue())
                        .joinedAt(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        participantRepository.saveAll(participantList);
    }

    @Override
    public void addParticipant(String conversationId, String participantId, ParticipantRole role) {
        if (hasPermission(conversationId)) {
            throw new AppException(ResourceException.ACCESS_DENIED);
        }
        if (participantRepository.existsByConversationIdAndAccountId(conversationId, participantId)) {
            throw new AppException(ResourceException.ENTITY_ALREADY_EXISTS);
        }
        Participant newParticipant = Participant.builder()
                .conversationId(conversationId)
                .accountId(participantId)
                .role(role)
                .joinedAt(LocalDateTime.now())
                .build();
        participantRepository.save(newParticipant);
    }

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

    public List<String> getParticipantIdsByConversationId(String conversationId) {
        return participantRepository.findAllAccountIdsByConversationId(conversationId);
    }

    @Override
    public Participant getOtherParticipantInPrivateConversation(String conversationId) {
        String currentAccountId = SecurityUtil.getCurrentUserId();
        return participantRepository.findOtherParticipantInPrivateConversation(conversationId, currentAccountId)
                .orElseThrow(() -> new AppException(ResourceException.ENTITY_NOT_FOUND));
    }

    @Override
    public void removeParticipant(String conversationId, String participantId) {
        Participant participant = participantRepository.findByConversationIdAndAccountId(conversationId, participantId)
                .orElseThrow(() -> new AppException(ResourceException.ENTITY_NOT_FOUND));

        String currentUserId = SecurityUtil.getCurrentUserId();
        boolean isAdmin = participantRepository.isAdmin(currentUserId, conversationId);
        if (currentUserId != null && !currentUserId.equals(participantId) && !isAdmin) {
            throw new AppException(ResourceException.ACCESS_DENIED);
        }
        participantRepository.delete(participant);
    }

}
