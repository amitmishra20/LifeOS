package com.lifeos.task.repository;

import com.lifeos.task.entity.Task;
import com.lifeos.task.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Task> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, TaskStatus status);

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    List<Task> findByUserIdAndStatusNot(Long userId, TaskStatus status);

    List<Task> findByGoalIdAndUserId(Long goalId, Long userId);

    List<Task> findByMilestoneIdAndUserId(Long milestoneId, Long userId);

    long countByUserIdAndStatus(Long userId, TaskStatus status);
}
