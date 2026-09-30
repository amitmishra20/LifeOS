package com.lifeos.habit.repository;

import com.lifeos.habit.entity.Habit;
import com.lifeos.habit.entity.HabitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    Optional<Habit> findByIdAndUserId(Long id, Long userId);

    List<Habit> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Habit> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, HabitStatus status);

    long countByUserIdAndStatus(Long userId, HabitStatus status);

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Habit h SET h.createdAt = :createdAt WHERE h.id = :id")
    void updateCreatedAt(@org.springframework.data.repository.query.Param("id") Long id, @org.springframework.data.repository.query.Param("createdAt") java.time.Instant createdAt);
}
