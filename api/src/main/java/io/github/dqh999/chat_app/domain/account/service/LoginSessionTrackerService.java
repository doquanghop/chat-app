package io.github.dqh999.chat_app.domain.account.service;


public interface LoginSessionTrackerService {
    void handleSession(String accountId);
}
