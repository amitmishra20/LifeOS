package com.lifeos.learning.repository;

import com.lifeos.learning.entity.LearningItem;
import com.lifeos.learning.entity.LearningStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningItemRepository extends JpaRepository<LearningItem, Long> {

    List<LearningItem> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<LearningItem> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, LearningStatus status);

    Optional<LearningItem> findByIdAndUserId(Long id, Long userId);

    long countByUserIdAndStatus(Long userId, LearningStatus status);

    long countByUserId(Long userId);
}
