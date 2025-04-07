package io.github.dqh999.chat_app.domain.message.data.repository;

import io.github.dqh999.chat_app.domain.message.data.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, String> {
    Page<Message> findAllByConversationId(String conversationId, Pageable pageable);
}
