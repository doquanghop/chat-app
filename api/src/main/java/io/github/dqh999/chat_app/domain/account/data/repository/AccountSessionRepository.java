package io.github.dqh999.chat_app.domain.account.data.repository;

import io.github.dqh999.chat_app.domain.account.data.model.AccountSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountSessionRepository extends JpaRepository<AccountSession, String> {
}
