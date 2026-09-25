package com.lifeos.milestone.repository;

import com.lifeos.milestone.entity.Milestone;
import com.lifeos.milestone.entity.MilestoneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    List<Milestone> findByGoalIdAndUserIdOrderByOrderIndexAscCreatedAtAsc(Long goalId, Long userId);

    Optional<Milestone> findByIdAndUserId(Long id, Long userId);

    long countByGoalIdAndUserId(Long goalId, Long userId);

    long countByGoalIdAndUserIdAndStatus(Long goalId, Long userId, MilestoneStatus status);

    Optional<Milestone> findTopByGoalIdAndUserIdOrderByOrderIndexDesc(Long goalId, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
