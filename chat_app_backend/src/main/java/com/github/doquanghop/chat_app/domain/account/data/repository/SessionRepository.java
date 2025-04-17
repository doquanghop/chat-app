package com.github.doquanghop.chat_app.domain.account.data.repository;

import com.github.doquanghop.chat_app.domain.account.data.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, String> {
    Optional<Session> findByDeviceId(String deviceId);

    boolean existsByAccountIdAndIsOnlineTrue(String accountId);

    @Query("SELECT MAX(s.lastSeen) FROM Session s WHERE s.accountId = :accountId")
    Optional<LocalDateTime> findLatestLastSeenByAccountId(@Param("accountId") String accountId);
}
