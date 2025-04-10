package io.github.dqh999.chat_app.domain.conversation.data.repository;

import io.github.dqh999.chat_app.domain.conversation.data.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, String> {
    @Query("SELECT p.accountId FROM Participant p WHERE p.conversationId = :conversationId")
    List<String> findAllAccountIdsByConversationId(@Param("conversationId") String conversationId);

    boolean existsByConversationIdAndAccountId(String conversationId, String accountId);

    @Query("""
                SELECT COUNT(m)
                FROM Message m
                JOIN Participant p ON m.conversationId = p.conversationId
                WHERE p.accountId = :accountId
                AND m.conversationId = :conversationId
                AND m.senderId != :accountId
                AND (p.lastSeenMessageId IS NULL
                OR m.createdAt > (
                SELECT m2.createdAt FROM Message m2 WHERE m2.conversationId = :conversationId
                ))
            """)
    long countUnreadMessagesByAccountIdAndConversationId(
            @Param("accountId") String accountId,
            @Param("conversationId") String conversationId
    );

    @Query("""
                SELECT CASE
                    WHEN p.lastSeenMessageId IS NULL THEN false
                    WHEN m.createdAt <= (
                        SELECT m2.createdAt FROM Message m2
                        WHERE m2.id = p.lastSeenMessageId
                    ) THEN true
                    ELSE false
                END
                FROM Message m
                JOIN Participant p ON m.conversationId = p.conversationId
                WHERE p.accountId = :accountId
                AND m.id = :messageId
                AND m.conversationId = :conversationId
            """)
    boolean isMessageSeenByAccountId(
            @Param("accountId") String accountId,
            @Param("conversationId") String conversationId,
            @Param("messageId") String messageId
    );
}
