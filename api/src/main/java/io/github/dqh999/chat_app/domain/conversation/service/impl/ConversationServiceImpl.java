package io.github.dqh999.chat_app.domain.conversation.service.impl;

import io.github.dqh999.chat_app.domain.conversation.data.model.Conversation;
import io.github.dqh999.chat_app.infrastructure.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class ConversationServiceImpl extends AbstractConversation  {

    @Override
    public PageResponse<Conversation> getAllConversations(int page, int size) {
        return super.getAllConversations(page, size);
    }

    @Override
    public boolean canSendMessage(String conversationId, String senderId) {
        return super.canSendMessage(conversationId, senderId);
    }

}
