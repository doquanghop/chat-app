package io.github.doquanghop.chat_app.application.api.conversation;

import io.github.doquanghop.chat_app.domain.conversation.service.ParticipantService;
import io.github.doquanghop.chat_app.infrastructure.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/participant")
@RequiredArgsConstructor
public class ParticipantController {
    private final ParticipantService participantService;

    @PutMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<Void>> updateNickName() {
        return null;
    }

}
