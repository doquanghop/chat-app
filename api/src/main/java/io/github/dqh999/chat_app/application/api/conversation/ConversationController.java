package io.github.dqh999.chat_app.application.api.conversation;

import io.github.dqh999.chat_app.domain.conversation.data.model.Conversation;
import io.github.dqh999.chat_app.domain.conversation.service.ConversationService;
import io.github.dqh999.chat_app.infrastructure.util.PageResponse;
import io.github.dqh999.chat_app.infrastructure.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/conversation")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Conversation>>> getConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        var response = conversationService.getAllConversations(page, pageSize);
        return ApiResponse.<PageResponse<Conversation>>build().withData(response).toEntity();
    }
}
