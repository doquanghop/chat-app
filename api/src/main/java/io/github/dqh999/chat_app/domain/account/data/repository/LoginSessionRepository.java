package io.github.dqh999.chat_app.domain.account.data.repository;

import io.github.dqh999.chat_app.domain.account.data.model.LoginSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginSessionRepository extends JpaRepository<LoginSession, String> {
//    Optional<LoginSession> findByAccountIdAndDeviceId(String accountId, String deviceId);
}
