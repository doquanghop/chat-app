package com.github.doquanghop.chat_app.application.api.message;

import com.github.doquanghop.chat_app.domain.message.data.dto.request.GetMessageRequest;
import com.github.doquanghop.chat_app.domain.message.data.dto.request.SendMessageRequest;
import com.github.doquanghop.chat_app.domain.message.data.dto.response.MessageResponse;
import com.github.doquanghop.chat_app.domain.message.service.MessageService;
import com.github.doquanghop.chat_app.infrastructure.annotation.logging.ActionLog;
import com.github.doquanghop.chat_app.infrastructure.model.PageResponse;
import com.github.doquanghop.chat_app.infrastructure.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/conversation/{conversationId}")
    @ActionLog
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @PathVariable String conversationId,
            @RequestBody SendMessageRequest request
    ) {
        var response = messageService.send(conversationId, request);
        return ApiResponse.<MessageResponse>build().withData(response).toEntity();
    }

    @GetMapping
    @ActionLog
    public ResponseEntity<ApiResponse<PageResponse<MessageResponse>>> getAllMessages(
            @RequestParam String conversationId,
            @RequestParam(defaultValue = "1") int page,
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
