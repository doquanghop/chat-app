package com.github.doquanghop.chat_app.domain.message.data.model;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @Column(name = "conversation_id")
    String conversationId;
    @Column(name = "sender_id")
    String senderId;
    String content;
    @Column(name = "created_at")
    LocalDateTime createdAt;
}
