package com.lifeos.calendar.service;

import com.lifeos.calendar.dto.CreateEventRequest;
import com.lifeos.calendar.dto.EventResponse;
import com.lifeos.calendar.dto.UnifiedCalendarItemDto;
import com.lifeos.calendar.dto.UpdateEventRequest;
import com.lifeos.calendar.entity.CalendarEvent;
import com.lifeos.calendar.entity.EventType;
import com.lifeos.calendar.repository.CalendarEventRepository;
import com.lifeos.common.exception.ResourceNotFoundException;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.learning.entity.LearningSession;
import com.lifeos.learning.repository.LearningSessionRepository;
import com.lifeos.task.entity.Task;
import com.lifeos.task.repository.TaskRepository;
import com.lifeos.user.entity.User;
import com.lifeos.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CalendarServiceImpl implements CalendarService {

    private final CalendarEventRepository eventRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;
    private final LearningSessionRepository learningSessionRepository;

    public CalendarServiceImpl(
            CalendarEventRepository eventRepository,
            UserRepository userRepository,
            TaskRepository taskRepository,
            GoalRepository goalRepository,
            LearningSessionRepository learningSessionRepository
    ) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.goalRepository = goalRepository;
        this.learningSessionRepository = learningSessionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEvents(Long userId, LocalDateTime start, LocalDateTime end, EventType eventType) {
        List<CalendarEvent> events;

        if (start != null && end != null) {
            if (eventType != null) {
                events = eventRepository.findByUserIdAndEventTypeAndDateRange(userId, eventType, start, end);
            } else {
                events = eventRepository.findByUserIdAndDateRange(userId, start, end);
            }
        } else if (eventType != null) {
            events = eventRepository.findByUserIdAndEventTypeOrderByStartTimeAsc(userId, eventType);
        } else {
            events = eventRepository.findByUserIdOrderByStartTimeAsc(userId);
        }

        return events.stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEvent(Long userId, Long id) {
        CalendarEvent event = eventRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return EventResponse.fromEntity(event);
    }

    @Override
    public EventResponse createEvent(Long userId, CreateEventRequest request) {
        if (request.getEndTime() != null && request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CalendarEvent event = new CalendarEvent(
                user,
                request.getTitle().trim(),
                request.getStartTime(),
                request.getEndTime(),
                request.getEventType() != null ? request.getEventType() : EventType.CUSTOM_EVENT
        );
        event.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);

        CalendarEvent saved = eventRepository.save(event);
        return EventResponse.fromEntity(saved);
    }

    @Override
    public EventResponse updateEvent(Long userId, Long id, UpdateEventRequest request) {
        CalendarEvent event = eventRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (request.getEndTime() != null && request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }

        event.setTitle(request.getTitle().trim());
        event.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        if (request.getEventType() != null) {
            event.setEventType(request.getEventType());
        }

        CalendarEvent saved = eventRepository.save(event);
        return EventResponse.fromEntity(saved);
    }

    @Override
    public void deleteEvent(Long userId, Long id) {
        CalendarEvent event = eventRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        eventRepository.delete(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnifiedCalendarItemDto> getUnifiedCalendarFeed(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now().plusMonths(2).withDayOfMonth(1);

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        List<UnifiedCalendarItemDto> items = new ArrayList<>();

        // 1. Custom Calendar Events
        List<CalendarEvent> events = eventRepository.findByUserIdAndDateRange(userId, startDateTime, endDateTime);
        for (CalendarEvent event : events) {
            items.add(new UnifiedCalendarItemDto(
                    "event-" + event.getId(),
                    event.getId(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getEventType() != null ? event.getEventType() : EventType.CUSTOM_EVENT,
                    "SCHEDULED",
                    "NORMAL",
                    "/calendar"
            ));
        }

        // 2. Task Deadlines
        List<Task> tasks = taskRepository.findByUserIdAndDueDateBetweenOrderByDueDateAsc(userId, start, end);
        for (Task task : tasks) {
            LocalDateTime taskTime = task.getDueDate().atTime(9, 0);
            items.add(new UnifiedCalendarItemDto(
                    "task-" + task.getId(),
                    task.getId(),
                    "Task: " + task.getTitle(),
                    task.getDescription(),
                    taskTime,
                    taskTime.plusMinutes(task.getEstimatedMinutes() != null ? task.getEstimatedMinutes() : 30),
                    EventType.TASK_DEADLINE,
                    task.getStatus().name(),
                    task.getPriority().name(),
                    "/tasks"
            ));
        }

        // 3. Goal Deadlines
        List<Goal> goals = goalRepository.findByUserIdAndTargetDateBetweenOrderByTargetDateAsc(userId, start, end);
        for (Goal goal : goals) {
            LocalDateTime goalTime = goal.getTargetDate().atTime(18, 0);
            items.add(new UnifiedCalendarItemDto(
                    "goal-" + goal.getId(),
                    goal.getId(),
                    "Goal Target: " + goal.getTitle(),
                    goal.getDescription(),
                    goalTime,
                    null,
                    EventType.GOAL_DEADLINE,
                    goal.getStatus().name(),
                    goal.getPriority().name(),
                    "/goals/" + goal.getId()
            ));
        }

        // 4. Learning Sessions
        List<LearningSession> sessions = learningSessionRepository.findByUserIdAndSessionDateBetweenOrderBySessionDateAsc(userId, start, end);
        for (LearningSession session : sessions) {
            LocalDateTime sessionTime = session.getSessionDate().atTime(14, 0);
            String itemTitle = session.getLearningItem() != null ? session.getLearningItem().getTitle() : "Learning";
            items.add(new UnifiedCalendarItemDto(
                    "learning-" + session.getId(),
                    session.getId(),
                    "Study: " + itemTitle + " (" + session.getTopic() + ")",
                    session.getNotes(),
                    sessionTime,
                    sessionTime.plusMinutes(session.getDurationMinutes()),
                    EventType.LEARNING_SESSION,
                    "COMPLETED",
                    "NORMAL",
                    "/learning"
            ));
        }

        items.sort(Comparator.comparing(UnifiedCalendarItemDto::getStartTime));
        return items;
    }
}
