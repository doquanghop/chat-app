package io.github.dqh999.chat_app.domain.user.data.repository;

import io.github.dqh999.chat_app.domain.user.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
