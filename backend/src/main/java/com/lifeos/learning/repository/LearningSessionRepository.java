package com.lifeos.learning.repository;

import com.lifeos.learning.entity.LearningSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningSessionRepository extends JpaRepository<LearningSession, Long> {

    List<LearningSession> findByLearningItemIdAndUserIdOrderBySessionDateDescCreatedAtDesc(Long learningItemId, Long userId);

    Optional<LearningSession> findByIdAndLearningItemIdAndUserId(Long id, Long learningItemId, Long userId);

    @Query("SELECT COALESCE(SUM(s.durationMinutes), 0) FROM LearningSession s WHERE s.learningItem.id = :learningItemId AND s.user.id = :userId")
    Integer sumDurationMinutesByLearningItemIdAndUserId(@Param("learningItemId") Long learningItemId, @Param("userId") Long userId);

    @Query("SELECT COUNT(s) FROM LearningSession s WHERE s.learningItem.id = :learningItemId AND s.user.id = :userId")
    long countByLearningItemIdAndUserId(@Param("learningItemId") Long learningItemId, @Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(s.durationMinutes), 0) FROM LearningSession s WHERE s.user.id = :userId")
    Integer sumDurationMinutesByUserId(@Param("userId") Long userId);

    List<LearningSession> findByUserIdAndSessionDateBetweenOrderBySessionDateAsc(Long userId, java.time.LocalDate start, java.time.LocalDate end);

    List<LearningSession> findByUserIdOrderBySessionDateDescCreatedAtDesc(Long userId);
}
