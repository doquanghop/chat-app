package io.github.dqh999.chat_app.application.api.conversation;

import io.github.dqh999.chat_app.domain.conversation.data.model.Conversation;
import io.github.dqh999.chat_app.domain.conversation.service.PrivateConversationService;
import io.github.dqh999.chat_app.infrastructure.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conversation/private")
@RequiredArgsConstructor
public class PrivateConversationController {
    private final PrivateConversationService privateConversationService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<Conversation>> getPrivateConversationByUserId(@PathVariable String userId) {
        var response = privateConversationService.getPrivateConversationWith(userId);
        return ApiResponse.<Conversation>build().withData(response).toEntity();
    }
}
