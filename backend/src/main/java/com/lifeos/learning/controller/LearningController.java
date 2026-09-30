package com.lifeos.learning.controller;

import com.lifeos.auth.dto.MessageResponse;
import com.lifeos.learning.dto.CreateLearningItemRequest;
import com.lifeos.learning.dto.CreateLearningSessionRequest;
import com.lifeos.learning.dto.LearningItemResponse;
import com.lifeos.learning.dto.LearningSessionResponse;
import com.lifeos.learning.dto.UpdateLearningItemRequest;
import com.lifeos.learning.dto.UpdateLearningProgressRequest;
import com.lifeos.learning.dto.UpdateLearningStatusRequest;
import com.lifeos.learning.entity.LearningStatus;
import com.lifeos.learning.service.LearningService;
import com.lifeos.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/learning")
public class LearningController {

    private final LearningService learningService;

    public LearningController(LearningService learningService) {
        this.learningService = learningService;
    }

    @GetMapping
    public ResponseEntity<List<LearningItemResponse>> getLearningItems(
            @RequestParam(required = false) LearningStatus status,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<LearningItemResponse> responses = learningService.getLearningItems(principal.getId(), status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningItemResponse> getLearningItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        LearningItemResponse response = learningService.getLearningItem(principal.getId(), id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<LearningItemResponse> createLearningItem(
            @Valid @RequestBody CreateLearningItemRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        LearningItemResponse response = learningService.createLearningItem(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LearningItemResponse> updateLearningItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLearningItemRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        LearningItemResponse response = learningService.updateLearningItem(principal.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<LearningItemResponse> updateLearningProgress(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLearningProgressRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        LearningItemResponse response = learningService.updateLearningProgress(principal.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<LearningItemResponse> updateLearningStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLearningStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        LearningItemResponse response = learningService.updateLearningStatus(principal.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteLearningItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        learningService.deleteLearningItem(principal.getId(), id);
        return ResponseEntity.ok(new MessageResponse("Learning item deleted successfully"));
    }

    @PostMapping("/{id}/sessions")
    public ResponseEntity<LearningSessionResponse> createLearningSession(
            @PathVariable Long id,
            @Valid @RequestBody CreateLearningSessionRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        LearningSessionResponse response = learningService.createLearningSession(principal.getId(), id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<List<LearningSessionResponse>> getLearningSessions(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<LearningSessionResponse> responses = learningService.getLearningSessions(principal.getId(), id);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}/sessions/{sessionId}")
    public ResponseEntity<MessageResponse> deleteLearningSession(
            @PathVariable Long id,
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        learningService.deleteLearningSession(principal.getId(), id, sessionId);
        return ResponseEntity.ok(new MessageResponse("Learning session deleted successfully"));
    }
}
