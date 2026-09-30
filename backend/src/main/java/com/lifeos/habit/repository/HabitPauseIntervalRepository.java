package com.lifeos.habit.repository;

import com.lifeos.habit.entity.HabitPauseInterval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitPauseIntervalRepository extends JpaRepository<HabitPauseInterval, Long> {

    List<HabitPauseInterval> findByHabitIdOrderByPausedAtAsc(Long habitId);

    Optional<HabitPauseInterval> findFirstByHabitIdAndResumedAtIsNullOrderByPausedAtDesc(Long habitId);
}
