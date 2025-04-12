package io.github.doquanghop.chat_app.domain.account.service;


public interface LoginSessionTrackerService {
    void handleSession(String accountId);
}
