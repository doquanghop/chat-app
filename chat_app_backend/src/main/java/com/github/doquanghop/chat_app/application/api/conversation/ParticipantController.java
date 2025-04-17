package com.github.doquanghop.chat_app.application.api.conversation;

import com.github.doquanghop.chat_app.domain.conversation.service.ParticipantService;
import com.github.doquanghop.chat_app.infrastructure.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/participant")
@RequiredArgsConstructor
public class ParticipantController {
    private final ParticipantService participantService;

    @PutMapping
    public ResponseEntity<ApiResponse<Void>> updateNickName() {
        return null;
    }

}
