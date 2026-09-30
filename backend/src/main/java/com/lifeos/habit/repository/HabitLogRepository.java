package com.lifeos.habit.repository;

import com.lifeos.habit.entity.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    Optional<HabitLog> findByHabitIdAndCompletionDate(Long habitId, LocalDate completionDate);

    Optional<HabitLog> findByHabitIdAndUserIdAndCompletionDate(Long habitId, Long userId, LocalDate completionDate);

    List<HabitLog> findByHabitIdOrderByCompletionDateAsc(Long habitId);

    List<HabitLog> findByHabitIdAndCompletionDateBetweenOrderByCompletionDateAsc(Long habitId, LocalDate startDate, LocalDate endDate);

    List<HabitLog> findByUserIdAndCompletionDate(Long userId, LocalDate completionDate);

    List<HabitLog> findByUserIdAndCompletionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    boolean existsByHabitIdAndCompletionDate(Long habitId, LocalDate completionDate);

    long countByUserIdAndCompletionDate(Long userId, LocalDate completionDate);
}
