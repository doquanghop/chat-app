package io.github.doquanghop.chat_app.domain.conversation.data.repository;

import io.github.doquanghop.chat_app.domain.conversation.data.model.Conversation;
import io.github.doquanghop.chat_app.domain.conversation.data.model.ConversationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, String> {
    @Query("""
            SELECT c FROM Conversation c
                INNER JOIN Participant p ON c.id = p.conversationId
                WHERE (:type IS NULL OR c.type = :type)
                AND p.accountId = :accountId
            """)
    Page<Conversation> findAllConversation(ConversationType type, String accountId, Pageable pageable);

    @Query("""
            SELECT c FROM Conversation c
                INNER JOIN Participant p1 ON c.id = p1.conversationId
                INNER JOIN Participant p2 ON c.id = p2.conversationId
                WHERE c.type = io.github.doquanghop.chat_app.domain.conversation.data.model.ConversationType.PRIVATE
                AND p1.accountId = :accountId
                AND p2.accountId = :targetAccountId
                AND p1.id != p2.id
            """)
    Optional<Conversation> findPrivateConversationBetween(String accountId, String targetAccountId);
}
