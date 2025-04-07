package io.github.dqh999.chat_app.domain.account.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;
    @Column(name = "hash_password")
    private String hashPassword;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}