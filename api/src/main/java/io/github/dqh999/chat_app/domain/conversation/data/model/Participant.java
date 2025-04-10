package io.github.dqh999.chat_app.domain.conversation.data.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "participants")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @Column(name = "conversation_id")
    String conversationId;
    @Column(name = "account_id")
    String accountId;
    @Column(name = "last_seen_message_id")
    String lastSeenMessageId;
    @Enumerated(EnumType.STRING)
    ParticipantRole role;
    String nickname;
    @Column(name = "joined_at")
    LocalDateTime joinedAt;
}
