package com.github.doquanghop.chat_app.application.api.conversation;

import com.github.doquanghop.chat_app.domain.conversation.data.dto.request.GetAllConversationRequest;
import com.github.doquanghop.chat_app.domain.conversation.data.dto.response.ConversationResponse;
import com.github.doquanghop.chat_app.domain.conversation.data.model.ConversationType;
import com.github.doquanghop.chat_app.domain.conversation.service.ConversationService;
import com.github.doquanghop.chat_app.infrastructure.model.PageResponse;
import com.github.doquanghop.chat_app.infrastructure.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conversation")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @GetMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<ConversationResponse>> getConversation(@PathVariable String conversationId) {
        var response = conversationService.getConversation(conversationId);
        return ApiResponse.<ConversationResponse>build().withData(response).toEntity();
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PageResponse<ConversationResponse>>> getConversations(
            @RequestParam(required = false) ConversationType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        var request = GetAllConversationRequest.builder()
                .type(type)
                .page(page)
                .pageSize(pageSize)
                .build();
        var response = conversationService.getAllConversations(request);
        return ApiResponse.<PageResponse<ConversationResponse>>build().withData(response).toEntity();
    }
}
