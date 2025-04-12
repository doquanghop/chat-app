package io.github.doquanghop.chat_app.application.api.message;

import io.github.doquanghop.chat_app.domain.message.data.dto.request.GetMessageRequest;
import io.github.doquanghop.chat_app.domain.message.data.dto.request.SendMessageRequest;
import io.github.doquanghop.chat_app.domain.message.data.dto.response.MessageResponse;
import io.github.doquanghop.chat_app.domain.message.service.MessageService;
import io.github.doquanghop.chat_app.infrastructure.model.PageResponse;
import io.github.doquanghop.chat_app.infrastructure.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/conversation/{conversationId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @PathVariable String conversationId,
            @RequestBody SendMessageRequest request
    ) {
        var response = messageService.send(conversationId, request);
        return ApiResponse.<MessageResponse>build().withData(response).toEntity();
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<PageResponse<MessageResponse>>> getAllMessages(
            @RequestParam String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        var request = GetMessageRequest.builder()
                .conversationId(conversationId)
                .page(page)
                .pageSize(pageSize)
                .build();
        var response = messageService.getAllMessages(request);
        return ApiResponse.<PageResponse<MessageResponse>>build().withData(response).toEntity();
    }

}
