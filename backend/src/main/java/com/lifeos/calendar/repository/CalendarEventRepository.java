package com.lifeos.calendar.repository;

import com.lifeos.calendar.entity.CalendarEvent;
import com.lifeos.calendar.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    List<CalendarEvent> findByUserIdOrderByStartTimeAsc(Long userId);

    Optional<CalendarEvent> findByIdAndUserId(Long id, Long userId);

    List<CalendarEvent> findByUserIdAndEventTypeOrderByStartTimeAsc(Long userId, EventType eventType);

    @Query("SELECT e FROM CalendarEvent e WHERE e.user.id = :userId AND e.startTime >= :start AND e.startTime <= :end ORDER BY e.startTime ASC")
    List<CalendarEvent> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT e FROM CalendarEvent e WHERE e.user.id = :userId AND e.eventType = :eventType AND e.startTime >= :start AND e.startTime <= :end ORDER BY e.startTime ASC")
    List<CalendarEvent> findByUserIdAndEventTypeAndDateRange(
            @Param("userId") Long userId,
            @Param("eventType") EventType eventType,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    long countByUserId(Long userId);
}
