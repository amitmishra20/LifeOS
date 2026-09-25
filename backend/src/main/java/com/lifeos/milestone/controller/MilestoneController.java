package com.lifeos.milestone.controller;

import com.lifeos.auth.dto.MessageResponse;
import com.lifeos.milestone.dto.CreateMilestoneRequest;
import com.lifeos.milestone.dto.MilestoneResponse;
import com.lifeos.milestone.dto.UpdateMilestoneRequest;
import com.lifeos.milestone.service.MilestoneService;
import com.lifeos.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MilestoneController {

    private final MilestoneService milestoneService;

    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    @PostMapping("/goals/{goalId}/milestones")
    public ResponseEntity<MilestoneResponse> createMilestone(
            @PathVariable Long goalId,
            @Valid @RequestBody CreateMilestoneRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        MilestoneResponse response = milestoneService.createMilestone(principal.getId(), goalId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/goals/{goalId}/milestones")
    public ResponseEntity<List<MilestoneResponse>> getMilestonesByGoal(
            @PathVariable Long goalId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<MilestoneResponse> responses = milestoneService.getMilestonesByGoal(principal.getId(), goalId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/milestones/{id}")
    public ResponseEntity<MilestoneResponse> getMilestoneById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        MilestoneResponse response = milestoneService.getMilestoneById(principal.getId(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/milestones/{id}")
    public ResponseEntity<MilestoneResponse> updateMilestone(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMilestoneRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        MilestoneResponse response = milestoneService.updateMilestone(principal.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/milestones/{id}")
    public ResponseEntity<MessageResponse> deleteMilestone(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        milestoneService.deleteMilestone(principal.getId(), id);
        return ResponseEntity.ok(new MessageResponse("Milestone deleted successfully"));
    }
}
