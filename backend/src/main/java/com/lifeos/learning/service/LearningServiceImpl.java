package com.lifeos.learning.service;

import com.lifeos.common.exception.ResourceNotFoundException;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.learning.dto.CreateLearningItemRequest;
import com.lifeos.learning.dto.CreateLearningSessionRequest;
import com.lifeos.learning.dto.LearningItemResponse;
import com.lifeos.learning.dto.LearningSessionResponse;
import com.lifeos.learning.dto.UpdateLearningItemRequest;
import com.lifeos.learning.dto.UpdateLearningProgressRequest;
import com.lifeos.learning.dto.UpdateLearningStatusRequest;
import com.lifeos.learning.entity.LearningCategory;
import com.lifeos.learning.entity.LearningItem;
import com.lifeos.learning.entity.LearningSession;
import com.lifeos.learning.entity.LearningStatus;
import com.lifeos.learning.repository.LearningItemRepository;
import com.lifeos.learning.repository.LearningSessionRepository;
import com.lifeos.user.entity.User;
import com.lifeos.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LearningServiceImpl implements LearningService {

    private final LearningItemRepository learningItemRepository;
    private final LearningSessionRepository learningSessionRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    public LearningServiceImpl(
            LearningItemRepository learningItemRepository,
            LearningSessionRepository learningSessionRepository,
            UserRepository userRepository,
            GoalRepository goalRepository
    ) {
        this.learningItemRepository = learningItemRepository;
        this.learningSessionRepository = learningSessionRepository;
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningItemResponse> getLearningItems(Long userId, LearningStatus status) {
        List<LearningItem> items;
        if (status != null) {
            items = learningItemRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
        } else {
            items = learningItemRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }

        return items.stream()
                .map(item -> toResponse(item, userId, false))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LearningItemResponse getLearningItem(Long userId, Long id) {
        LearningItem item = findItemOrThrow(id, userId);
        return toResponse(item, userId, true);
    }

    @Override
    @Transactional
    public LearningItemResponse createLearningItem(Long userId, CreateLearningItemRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        int targetProgress = request.getTargetProgress() != null ? request.getTargetProgress() : 100;
        if (targetProgress < 1 || targetProgress > 100) {
            throw new IllegalArgumentException("Target progress must be between 1 and 100");
        }

        int currentProgress = request.getCurrentProgress() != null ? request.getCurrentProgress() : 0;
        if (currentProgress < 0 || currentProgress > 100) {
            throw new IllegalArgumentException("Current progress must be between 0 and 100");
        }

        LearningItem item = new LearningItem();
        item.setUser(user);
        item.setTitle(request.getTitle().trim());
        item.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        item.setCategory(request.getCategory() != null ? request.getCategory() : LearningCategory.TECHNICAL);
        item.setTargetProgress(targetProgress);
        item.setCurrentProgress(currentProgress);
        item.setStatus(LearningStatus.ACTIVE);

        if (request.getGoalId() != null) {
            Goal goal = goalRepository.findByIdAndUserId(request.getGoalId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
            item.setGoal(goal);
        }

        LearningItem saved = learningItemRepository.save(item);
        return toResponse(saved, userId, false);
    }

    @Override
    @Transactional
    public LearningItemResponse updateLearningItem(Long userId, Long id, UpdateLearningItemRequest request) {
        LearningItem item = findItemOrThrow(id, userId);

        if (request.getTargetProgress() != null) {
            if (request.getTargetProgress() < 1 || request.getTargetProgress() > 100) {
                throw new IllegalArgumentException("Target progress must be between 1 and 100");
            }
            item.setTargetProgress(request.getTargetProgress());
        }

        item.setTitle(request.getTitle().trim());
        item.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        if (request.getCategory() != null) {
            item.setCategory(request.getCategory());
        }

        if (request.getGoalId() != null) {
            Goal goal = goalRepository.findByIdAndUserId(request.getGoalId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
            item.setGoal(goal);
        } else {
            item.setGoal(null);
        }

        LearningItem saved = learningItemRepository.save(item);
        return toResponse(saved, userId, false);
    }

    @Override
    @Transactional
    public LearningItemResponse updateLearningProgress(Long userId, Long id, UpdateLearningProgressRequest request) {
        LearningItem item = findItemOrThrow(id, userId);

        if (request.getCurrentProgress() == null || request.getCurrentProgress() < 0 || request.getCurrentProgress() > 100) {
            throw new IllegalArgumentException("Current progress must be between 0 and 100");
        }

        item.setCurrentProgress(request.getCurrentProgress());
        LearningItem saved = learningItemRepository.save(item);
        return toResponse(saved, userId, false);
    }

    @Override
    @Transactional
    public LearningItemResponse updateLearningStatus(Long userId, Long id, UpdateLearningStatusRequest request) {
        LearningItem item = findItemOrThrow(id, userId);

        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        validateStatusTransition(item.getStatus(), request.getStatus());
        item.setStatus(request.getStatus());

        LearningItem saved = learningItemRepository.save(item);
        return toResponse(saved, userId, false);
    }

    @Override
    @Transactional
    public void deleteLearningItem(Long userId, Long id) {
        LearningItem item = findItemOrThrow(id, userId);
        learningItemRepository.delete(item);
    }

    @Override
    @Transactional
    public LearningSessionResponse createLearningSession(Long userId, Long learningItemId, CreateLearningSessionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LearningItem item = findItemOrThrow(learningItemId, userId);

        if (item.getStatus() == LearningStatus.ARCHIVED) {
            throw new IllegalArgumentException("Cannot log session for an archived learning item");
        }

        if (request.getSessionDate() == null) {
            throw new IllegalArgumentException("Session date is required");
        }

        if (request.getSessionDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Session date cannot be in the future");
        }

        if (request.getDurationMinutes() == null || request.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }

        if (request.getTopic() == null || request.getTopic().trim().isEmpty()) {
            throw new IllegalArgumentException("Topic is required");
        }

        LearningSession session = new LearningSession();
        session.setLearningItem(item);
        session.setUser(user);
        session.setSessionDate(request.getSessionDate());
        session.setDurationMinutes(request.getDurationMinutes());
        session.setTopic(request.getTopic().trim());
        session.setNotes(request.getNotes() != null ? request.getNotes().trim() : null);

        LearningSession saved = learningSessionRepository.save(session);

        if (request.getNewProgress() != null) {
            if (request.getNewProgress() < 0 || request.getNewProgress() > 100) {
                throw new IllegalArgumentException("Current progress must be between 0 and 100");
            }
            item.setCurrentProgress(request.getNewProgress());
            learningItemRepository.save(item);
        }

        return LearningSessionResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningSessionResponse> getLearningSessions(Long userId, Long learningItemId) {
        findItemOrThrow(learningItemId, userId);

        return learningSessionRepository
                .findByLearningItemIdAndUserIdOrderBySessionDateDescCreatedAtDesc(learningItemId, userId)
                .stream()
                .map(LearningSessionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteLearningSession(Long userId, Long learningItemId, Long sessionId) {
        findItemOrThrow(learningItemId, userId);

        LearningSession session = learningSessionRepository
                .findByIdAndLearningItemIdAndUserId(sessionId, learningItemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning session not found"));

        learningSessionRepository.delete(session);
    }

    private void validateStatusTransition(LearningStatus current, LearningStatus target) {
        boolean valid = (current == LearningStatus.ACTIVE && (target == LearningStatus.PAUSED || target == LearningStatus.COMPLETED || target == LearningStatus.ARCHIVED))
                || (current == LearningStatus.PAUSED && (target == LearningStatus.ACTIVE || target == LearningStatus.ARCHIVED))
                || (current == LearningStatus.COMPLETED && (target == LearningStatus.ACTIVE || target == LearningStatus.ARCHIVED))
                || (current == LearningStatus.ARCHIVED && target == LearningStatus.ACTIVE);

        if (!valid) {
            throw new IllegalArgumentException(String.format("Invalid status transition from %s to %s", current, target));
        }
    }

    private LearningItem findItemOrThrow(Long id, Long userId) {
        return learningItemRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning item not found"));
    }

    private LearningItemResponse toResponse(LearningItem item, Long userId, boolean includeSessions) {
        Integer totalMinutes = learningSessionRepository.sumDurationMinutesByLearningItemIdAndUserId(item.getId(), userId);
        long sessionCount = learningSessionRepository.countByLearningItemIdAndUserId(item.getId(), userId);

        List<LearningSessionResponse> recentSessions = null;
        if (includeSessions) {
            recentSessions = learningSessionRepository
                    .findByLearningItemIdAndUserIdOrderBySessionDateDescCreatedAtDesc(item.getId(), userId)
                    .stream()
                    .map(LearningSessionResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        return LearningItemResponse.fromEntity(item, totalMinutes, sessionCount, recentSessions);
    }
}
