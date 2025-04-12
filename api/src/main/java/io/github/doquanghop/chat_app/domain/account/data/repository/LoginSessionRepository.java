package io.github.doquanghop.chat_app.domain.account.data.repository;

import io.github.doquanghop.chat_app.domain.account.data.model.LoginSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginSessionRepository extends JpaRepository<LoginSession, String> {
//    Optional<LoginSession> findByAccountIdAndDeviceId(String accountId, String deviceId);
}
