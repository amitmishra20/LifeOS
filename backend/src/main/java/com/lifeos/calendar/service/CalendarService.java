package com.lifeos.calendar.service;

import com.lifeos.calendar.dto.CreateEventRequest;
import com.lifeos.calendar.dto.EventResponse;
import com.lifeos.calendar.dto.UnifiedCalendarItemDto;
import com.lifeos.calendar.dto.UpdateEventRequest;
import com.lifeos.calendar.entity.EventType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CalendarService {

    List<EventResponse> getEvents(Long userId, LocalDateTime start, LocalDateTime end, EventType eventType);

    EventResponse getEvent(Long userId, Long id);

    EventResponse createEvent(Long userId, CreateEventRequest request);

    EventResponse updateEvent(Long userId, Long id, UpdateEventRequest request);

    void deleteEvent(Long userId, Long id);

    List<UnifiedCalendarItemDto> getUnifiedCalendarFeed(Long userId, LocalDate startDate, LocalDate endDate);
}
