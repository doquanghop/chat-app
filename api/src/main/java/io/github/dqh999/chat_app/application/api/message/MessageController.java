package io.github.dqh999.chat_app.application.api.message;

import io.github.dqh999.chat_app.domain.message.data.dto.request.SendMessageRequest;
import io.github.dqh999.chat_app.domain.message.data.dto.response.MessageResponse;
import io.github.dqh999.chat_app.domain.message.service.MessageService;
import io.github.dqh999.chat_app.infrastructure.utils.PageResponse;
import io.github.dqh999.chat_app.infrastructure.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(@RequestBody SendMessageRequest request) {
        var response = messageService.send(request);
        return ApiResponse.<MessageResponse>build().withData(response).toEntity();
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<PageResponse<MessageResponse>>> getAllMessages(
            @RequestParam String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var response = messageService.getAllMessages(conversationId, page, size);
        return ApiResponse.<PageResponse<MessageResponse>>build().withData(response).toEntity();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<PageResponse<MessageResponse>>> search(
            @RequestParam String conversationId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var response = messageService.search(conversationId, keyword, page, size);
        return ApiResponse.<PageResponse<MessageResponse>>build().withData(response).toEntity();
    }
}
