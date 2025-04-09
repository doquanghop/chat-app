package io.github.dqh999.chat_app.domain.user.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;
    @Column(name = "user_name", unique = true)
    private String userName;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "avatar_url")
    private String avatarURL;
    @Column(name = "is_online")
    private Boolean isOnline;
    @Column(name = "last_seen")
    private LocalDateTime lastSeen;
}