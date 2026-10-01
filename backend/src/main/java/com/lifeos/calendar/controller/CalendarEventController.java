package com.lifeos.calendar.controller;

import com.lifeos.auth.dto.MessageResponse;
import com.lifeos.calendar.dto.CreateEventRequest;
import com.lifeos.calendar.dto.EventResponse;
import com.lifeos.calendar.dto.UnifiedCalendarItemDto;
import com.lifeos.calendar.dto.UpdateEventRequest;
import com.lifeos.calendar.entity.EventType;
import com.lifeos.calendar.service.CalendarService;
import com.lifeos.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/calendar")
public class CalendarEventController {

    private final CalendarService calendarService;

    public CalendarEventController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    /**
     * Unified calendar aggregation endpoint:
     * GET /api/v1/calendar?start=YYYY-MM-DD&end=YYYY-MM-DD
     */
    @GetMapping
    public ResponseEntity<List<UnifiedCalendarItemDto>> getUnifiedCalendarFeed(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        LocalDate effectiveStart = start != null ? start : startDate;
        LocalDate effectiveEnd = end != null ? end : endDate;
        List<UnifiedCalendarItemDto> feed = calendarService.getUnifiedCalendarFeed(principal.getId(), effectiveStart, effectiveEnd);
        return ResponseEntity.ok(feed);
    }

    /**
     * List user custom events:
     * GET /api/v1/calendar/events
     */
    @GetMapping("/events")
    public ResponseEntity<List<EventResponse>> getEvents(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) EventType type,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<EventResponse> events = calendarService.getEvents(principal.getId(), start, end, type);
        return ResponseEntity.ok(events);
    }

    /**
     * Get single custom event by ID:
     * GET /api/v1/calendar/events/{id}
     */
    @GetMapping("/events/{id}")
    public ResponseEntity<EventResponse> getEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        EventResponse response = calendarService.getEvent(principal.getId(), id);
        return ResponseEntity.ok(response);
    }

    /**
     * Create a new custom event:
     * POST /api/v1/calendar/events
     */
    @PostMapping("/events")
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        EventResponse response = calendarService.createEvent(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update custom event:
     * PUT /api/v1/calendar/events/{id}
     */
    @PutMapping("/events/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        EventResponse response = calendarService.updateEvent(principal.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete custom event:
     * DELETE /api/v1/calendar/events/{id}
     */
    @DeleteMapping("/events/{id}")
    public ResponseEntity<MessageResponse> deleteEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        calendarService.deleteEvent(principal.getId(), id);
        return ResponseEntity.ok(new MessageResponse("Event deleted successfully"));
    }
}
