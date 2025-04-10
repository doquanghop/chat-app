package io.github.dqh999.chat_app.domain.conversation.service.impl;

import io.github.dqh999.chat_app.domain.conversation.data.dto.response.ConversationResponse;
import io.github.dqh999.chat_app.domain.conversation.data.model.Conversation;
import io.github.dqh999.chat_app.domain.conversation.data.model.ConversationType;
import io.github.dqh999.chat_app.domain.conversation.data.model.Participant;
import io.github.dqh999.chat_app.domain.conversation.data.model.ParticipantRole;
import io.github.dqh999.chat_app.domain.conversation.data.repository.ConversationRepository;
import io.github.dqh999.chat_app.domain.conversation.data.repository.ParticipantRepository;
import io.github.dqh999.chat_app.domain.conversation.service.ConversationService;
import io.github.dqh999.chat_app.domain.conversation.service.ParticipantService;
import io.github.dqh999.chat_app.domain.message.data.model.Message;
import io.github.dqh999.chat_app.domain.user.service.UserService;
import io.github.dqh999.chat_app.infrastructure.constant.QualifierNames;
import io.github.dqh999.chat_app.infrastructure.model.AppException;
import io.github.dqh999.chat_app.infrastructure.service.MessagePublisher;
import io.github.dqh999.chat_app.infrastructure.utils.PageResponse;
import io.github.dqh999.chat_app.infrastructure.utils.ResourceException;
import io.github.dqh999.chat_app.infrastructure.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractConversation implements ConversationService {
    @Autowired
    protected UserService userService;
    @Autowired
    private @Qualifier(QualifierNames.WEBSOCKET_MESSAGE_PUBLISHER) MessagePublisher webSocketMessagePublisher;
    @Autowired
    protected ConversationRepository conversationRepository;
    @Autowired
    protected ParticipantRepository participantRepository;
    @Autowired
    protected ParticipantService participantService;


    @Override
    public PageResponse<ConversationResponse> getAllConversations(int page, int size) {
        String currentAccountId = SecurityUtil.getCurrentUserId();
        log.debug("Fetching all conversations for userId={}, page={}, size={}", currentAccountId, page, size);
        PageRequest pageRequest = PageRequest.of(page, size);
        var conversationPage = conversationRepository.findAllConversation(currentAccountId, pageRequest);
        var responses = conversationPage.stream().map(conversation -> {
            var response = ConversationResponse.builder().build();
            long unreadCount = participantService.getUnreadMessageCount(conversation.getId());
            response.setUnreadCount(unreadCount);
            return response;
        }).collect(Collectors.toList());
        return PageResponse.<ConversationResponse>builder()
                .hasNext(conversationPage.hasNext())
                .hasPrevious(conversationPage.hasPrevious())
                .data(responses)
                .build();
    }

    @Override
    public void checkSenderPermission(String conversationId, String senderId) {
        log.debug("Checking if sender has permission: senderId=****, conversationId=****");
        if (!conversationRepository.existsById(conversationId)) {
            log.warn("Conversation with ID [{}] not found", conversationId);
            throw new AppException(ResourceException.ENTITY_NOT_FOUND);
        }

        if (!participantRepository
                .existsByConversationIdAndAccountId(conversationId, senderId)) {
            log.warn("Access denied: senderId=**** is not a participant of conversationId=****");
            throw new AppException(ResourceException.ACCESS_DENIED, "You are not a participant of this conversation");
        }
        log.debug("Sender permission check passed");
    }

    @Override
    public void sendNotification(String conversationId, Message message) {

    }

    private List<String> getParticipant(String conversationId) {
        return participantRepository.findAllAccountIdsByConversationId(conversationId);
    }

    protected Conversation createAndSave(ConversationType type, String name, Map<String, ParticipantRole> participants) {
        Conversation conversation = Conversation.builder()
                .type(type)
                .name(name)
                .build();
        conversationRepository.save(conversation);
        for (Map.Entry<String, ParticipantRole> entry : participants.entrySet()) {
            Participant participant = Participant.builder()
                    .conversationId(conversation.getId())
                    .accountId(entry.getKey())
                    .role(entry.getValue())
                    .joinedAt(LocalDateTime.now())
                    .build();
            participantRepository.save(participant);
        }
        return conversation;
    }
}
