package com.lifeos.habit.controller;

import com.lifeos.auth.dto.MessageResponse;
import com.lifeos.habit.dto.CreateHabitRequest;
import com.lifeos.habit.dto.HabitDayHistoryDto;
import com.lifeos.habit.dto.HabitResponse;
import com.lifeos.habit.dto.HabitToggleResponse;
import com.lifeos.habit.dto.UpdateHabitRequest;
import com.lifeos.habit.dto.UpdateHabitStatusRequest;
import com.lifeos.habit.entity.HabitStatus;
import com.lifeos.habit.service.HabitService;
import com.lifeos.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @PostMapping
    public ResponseEntity<HabitResponse> createHabit(
            @Valid @RequestBody CreateHabitRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        HabitResponse response = habitService.createHabit(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<HabitResponse>> getHabits(
            @RequestParam(required = false) HabitStatus status,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<HabitResponse> responses = habitService.getHabits(principal.getId(), status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/today")
    public ResponseEntity<List<HabitResponse>> getTodayHabits(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<HabitResponse> responses = habitService.getTodayHabits(principal.getId());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitResponse> getHabitById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        HabitResponse response = habitService.getHabitById(principal.getId(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitResponse> updateHabit(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHabitRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        HabitResponse response = habitService.updateHabit(principal.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<HabitResponse> updateHabitStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHabitStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        HabitResponse response = habitService.updateHabitStatus(principal.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteHabit(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        habitService.deleteHabit(principal.getId(), id);
        return ResponseEntity.ok(new MessageResponse("Habit deleted successfully"));
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<HabitToggleResponse> toggleHabit(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        HabitToggleResponse response = habitService.toggleHabit(principal.getId(), id, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<HabitDayHistoryDto>> getHabitHistory(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<HabitDayHistoryDto> history = habitService.getHabitHistory(principal.getId(), id);
        return ResponseEntity.ok(history);
    }
}
