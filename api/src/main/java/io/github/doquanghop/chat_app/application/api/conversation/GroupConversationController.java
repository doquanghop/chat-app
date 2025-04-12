package io.github.doquanghop.chat_app.application.api.conversation;

import io.github.doquanghop.chat_app.domain.conversation.data.dto.request.CreateConversationGroupRequest;
import io.github.doquanghop.chat_app.domain.conversation.data.model.Conversation;
import io.github.doquanghop.chat_app.domain.conversation.service.GroupConversationService;
import io.github.doquanghop.chat_app.infrastructure.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conversation/group")
@RequiredArgsConstructor
public class GroupConversationController {
    private final GroupConversationService groupConversationService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<Conversation>> create(@RequestBody CreateConversationGroupRequest request) {
        var response = groupConversationService.create(request);
        return ApiResponse.<Conversation>build().withData(response).toEntity();
    }

    @PostMapping("/{conversationId}/leave")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<Void>> leaveConversation(@PathVariable String conversationId) {
        groupConversationService.leftConversation(conversationId);
        return ApiResponse.<Void>build()
                .withMessage("Left conversation successfully")
                .toEntity();
    }

    @PostMapping("/{conversationId}/participants")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<Void>> addParticipant(
            @PathVariable String conversationId,
            @RequestParam String participantId
    ) {
        groupConversationService.addParticipant(conversationId, participantId);
        return ApiResponse.<Void>build()
                .withMessage("Participant added successfully")
                .toEntity();
    }

    @DeleteMapping("/{conversationId}/participants/{participantId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<Void>> removeParticipant(
            @PathVariable String conversationId,
            @PathVariable String participantId
    ) {
        groupConversationService.removeParticipant(conversationId, participantId);
        return ApiResponse.<Void>build()
                .withMessage("Participant removed successfully")
                .toEntity();
    }
}
