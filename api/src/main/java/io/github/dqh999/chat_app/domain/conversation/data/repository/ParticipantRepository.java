package io.github.dqh999.chat_app.domain.conversation.data.repository;

import io.github.dqh999.chat_app.domain.conversation.data.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, String> {
    @Query("SELECT p.accountId FROM Participant p WHERE p.conversationId = :conversationId")
    List<String> findAllAccountIdsByConversationId(@Param("conversationId") String conversationId);

}
