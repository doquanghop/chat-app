package io.github.doquanghop.chat_app.application.api.conversation;

import io.github.doquanghop.chat_app.domain.conversation.data.dto.response.ConversationResponse;
import io.github.doquanghop.chat_app.domain.conversation.service.PrivateConversationService;
import io.github.doquanghop.chat_app.infrastructure.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conversation/private")
@RequiredArgsConstructor
public class PrivateConversationController {
    private final PrivateConversationService privateConversationService;

    @GetMapping("/user/{counterpartId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<ConversationResponse>> getPrivateConversationByUserId(@PathVariable String counterpartId) {
        var response = privateConversationService.getPrivateConversationWith(counterpartId);
        return ApiResponse.<ConversationResponse>build().withData(response).toEntity();
    }
}
