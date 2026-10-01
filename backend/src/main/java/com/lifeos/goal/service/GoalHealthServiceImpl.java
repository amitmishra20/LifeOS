package com.lifeos.goal.service;

import com.lifeos.common.exception.ResourceNotFoundException;
import com.lifeos.goal.config.GoalHealthProperties;
import com.lifeos.goal.dto.GoalHealthEvaluationDto;
import com.lifeos.goal.dto.GoalHealthOverviewResponse;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalHealth;
import com.lifeos.goal.entity.GoalStatus;
import com.lifeos.goal.repository.GoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoalHealthServiceImpl implements GoalHealthService {

    private final GoalRepository goalRepository;
    private final GoalHealthProperties properties;

    public GoalHealthServiceImpl(GoalRepository goalRepository, GoalHealthProperties properties) {
        this.goalRepository = goalRepository;
        this.properties = properties;
    }

    @Override
    public GoalHealth evaluate(Goal goal) {
        return evaluate(goal, LocalDate.now());
    }

    @Override
    public GoalHealth evaluate(Goal goal, LocalDate evaluationDate) {
        if (goal == null) {
            return GoalHealth.ON_TRACK;
        }
        return evaluateWithDetails(goal, evaluationDate).getHealth();
    }

    @Override
    public GoalHealthEvaluationDto evaluateWithDetails(Goal goal) {
        return evaluateWithDetails(goal, LocalDate.now());
    }

    @Override
    public GoalHealthEvaluationDto evaluateWithDetails(Goal goal, LocalDate evaluationDate) {
        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null for health evaluation");
        }

        LocalDate today = evaluationDate != null ? evaluationDate : LocalDate.now();
        double actualProgress = goal.getProgress() != null ? round1dp(goal.getProgress()) : 0.0;
        LocalDate targetDate = goal.getTargetDate();

        LocalDate effectiveStartDate = null;
        if (goal.getStartDate() != null) {
            effectiveStartDate = goal.getStartDate();
        } else if (goal.getCreatedAt() != null) {
            effectiveStartDate = goal.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        Long totalDays = (effectiveStartDate != null && targetDate != null)
                ? ChronoUnit.DAYS.between(effectiveStartDate, targetDate)
                : null;
        Long elapsedDays = effectiveStartDate != null
                ? Math.max(0L, ChronoUnit.DAYS.between(effectiveStartDate, today))
                : null;
        Long remainingDays = targetDate != null
                ? ChronoUnit.DAYS.between(today, targetDate)
                : null;

        GoalHealth health;
        double expectedProgress;
        double delta;
        String reason;

        // STEP 1 — COMPLETED
        if (actualProgress >= 100.0 || goal.getStatus() == GoalStatus.COMPLETED) {
            health = GoalHealth.COMPLETED;
            expectedProgress = 100.0;
            delta = round1dp(actualProgress - 100.0);
            reason = "Goal is completed.";
        }
        // STEP 2 — OVERDUE / PAST TARGET DATE
        else if (targetDate != null && today.isAfter(targetDate)) {
            health = GoalHealth.BEHIND;
            expectedProgress = 100.0;
            delta = round1dp(actualProgress - 100.0);
            reason = "Target date has passed (" + targetDate + ") and the goal is not completed.";
        }
        // STEP 3 — NO TARGET DATE
        else if (targetDate == null) {
            health = GoalHealth.ON_TRACK;
            expectedProgress = 0.0;
            delta = round1dp(actualProgress - 0.0);
            reason = "No target date set; goal has no deadline constraints.";
        }
        // DATA INTEGRITY FALLBACK — BOTH START DATE AND CREATED AT NULL
        else if (effectiveStartDate == null) {
            expectedProgress = 0.0;
            delta = round1dp(actualProgress - 0.0);
            health = classifyDelta(delta);
            reason = "Missing start date and creation date anchor; evaluated against deadline.";
        }
        // STEP 4 — INVALID DATE RANGE (D < S)
        else if (targetDate.isBefore(effectiveStartDate)) {
            if (!today.isBefore(targetDate)) {
                health = GoalHealth.BEHIND;
                expectedProgress = 100.0;
                delta = round1dp(actualProgress - 100.0);
            } else {
                expectedProgress = 0.0;
                delta = round1dp(actualProgress - 0.0);
                health = classifyDelta(delta);
            }
            reason = "Target date (" + targetDate + ") precedes start date (" + effectiveStartDate + "); evaluated safely.";
        }
        // STEP 5 — ZERO-DURATION GOAL (S == D)
        else if (effectiveStartDate.isEqual(targetDate)) {
            if (today.isBefore(targetDate)) {
                expectedProgress = 0.0;
                delta = round1dp(actualProgress - 0.0);
                health = classifyDelta(delta);
                reason = "Single-day goal scheduled for future date (" + targetDate + ").";
            } else if (today.isEqual(targetDate)) {
                expectedProgress = 100.0;
                delta = round1dp(actualProgress - 100.0);
                health = classifyDelta(delta);
                reason = "Single-day goal due today (" + targetDate + "); expected 100% completion.";
            } else {
                health = GoalHealth.BEHIND;
                expectedProgress = 100.0;
                delta = round1dp(actualProgress - 100.0);
                reason = "Target date has passed (" + targetDate + ") and the goal is not completed.";
            }
        }
        // STEP 6 — NORMAL SCHEDULE (S < D)
        else {
            if (today.isBefore(effectiveStartDate)) {
                elapsedDays = 0L;
                expectedProgress = 0.0;
                delta = round1dp(actualProgress - 0.0);
                health = classifyDelta(delta);
                reason = "Goal has not started yet (scheduled start: " + effectiveStartDate + ").";
            } else {
                long total = ChronoUnit.DAYS.between(effectiveStartDate, targetDate);
                long elapsed = ChronoUnit.DAYS.between(effectiveStartDate, today);
                double rawExpected = ((double) elapsed * 100.0) / (double) total;
                expectedProgress = Math.max(0.0, Math.min(100.0, round1dp(rawExpected)));
                delta = round1dp(actualProgress - expectedProgress);
                health = classifyDelta(delta);

                if (health == GoalHealth.ON_TRACK) {
                    reason = "Progress (" + actualProgress + "%) meets or exceeds expected progress (" + expectedProgress + "%).";
                } else if (health == GoalHealth.AT_RISK) {
                    reason = "Progress (" + actualProgress + "%) is slightly behind expected progress (" + expectedProgress + "%).";
                } else {
                    reason = "Progress (" + actualProgress + "%) is significantly behind expected progress (" + expectedProgress + "%).";
                }
            }
        }

        GoalHealthEvaluationDto dto = new GoalHealthEvaluationDto();
        dto.setGoalId(goal.getId());
        dto.setGoalTitle(goal.getTitle());
        dto.setCategory(goal.getCategory());
        dto.setStatus(goal.getStatus());
        dto.setHealth(health);
        dto.setActualProgress(actualProgress);
        dto.setExpectedProgress(expectedProgress);
        dto.setDelta(delta);
        dto.setStartDate(goal.getStartDate());
        dto.setTargetDate(goal.getTargetDate());
        dto.setEffectiveStartDate(effectiveStartDate);
        dto.setTotalDays(totalDays);
        dto.setElapsedDays(elapsedDays);
        dto.setRemainingDays(remainingDays);
        dto.setReason(reason);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public GoalHealthOverviewResponse getGoalHealthOverview(Long userId) {
        List<Goal> goals = goalRepository.findByUserIdOrderByCreatedAtDesc(userId);
        LocalDate today = LocalDate.now();

        List<GoalHealthEvaluationDto> evaluations = new ArrayList<>(goals.size());
        int onTrack = 0;
        int atRisk = 0;
        int behind = 0;
        int completed = 0;

        for (Goal goal : goals) {
            GoalHealthEvaluationDto eval = evaluateWithDetails(goal, today);
            evaluations.add(eval);
            switch (eval.getHealth()) {
                case ON_TRACK -> onTrack++;
                case AT_RISK -> atRisk++;
                case BEHIND -> behind++;
                case COMPLETED -> completed++;
            }
        }

        GoalHealthOverviewResponse response = new GoalHealthOverviewResponse();
        response.setTotalGoals(goals.size());
        response.setOnTrackCount(onTrack);
        response.setAtRiskCount(atRisk);
        response.setBehindCount(behind);
        response.setCompletedCount(completed);
        response.setEvaluations(evaluations);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public GoalHealthEvaluationDto getGoalHealth(Long userId, Long goalId) {
        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        return evaluateWithDetails(goal, LocalDate.now());
    }

    private GoalHealth classifyDelta(double delta) {
        if (delta >= properties.getAtRiskThreshold()) {
            return GoalHealth.ON_TRACK;
        } else if (delta >= properties.getBehindThreshold()) {
            return GoalHealth.AT_RISK;
        } else {
            return GoalHealth.BEHIND;
        }
    }

    private double round1dp(double value) {
        return BigDecimal.valueOf(value)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
