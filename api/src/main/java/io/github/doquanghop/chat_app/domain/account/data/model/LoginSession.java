package io.github.doquanghop.chat_app.domain.account.data.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_sessions")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "account_id")
    String accountId;

    @Column(name = "device_id")
    String deviceId;

    @Column(name = "ip_address")
    String ipAddress;

    @Column(name = "user_agent")
    String userAgent;

    @Column(name = "login_at")
    LocalDateTime loginAt;

    @Column(name = "is_active")
    boolean isActive;

}