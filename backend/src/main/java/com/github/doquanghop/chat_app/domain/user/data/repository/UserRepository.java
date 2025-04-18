package com.github.doquanghop.chat_app.domain.user.data.repository;

import com.github.doquanghop.chat_app.domain.user.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserName(String userName);

    boolean existsByUserName(String userName);
}
