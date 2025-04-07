package io.github.dqh999.chat_app.domain.message.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    String id;
    @Column(name = "conversation_id")
    String conversationId;
    @Column(name = "sender_id")
    String senderId;
    String content;
    @Column(name = "created_at")
    LocalDateTime createdAt;
}
