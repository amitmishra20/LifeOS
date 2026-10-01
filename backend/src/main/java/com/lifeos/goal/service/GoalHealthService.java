package com.lifeos.goal.service;

import com.lifeos.goal.dto.GoalHealthEvaluationDto;
import com.lifeos.goal.dto.GoalHealthOverviewResponse;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalHealth;

import java.time.LocalDate;

public interface GoalHealthService {

    GoalHealth evaluate(Goal goal);

    GoalHealth evaluate(Goal goal, LocalDate evaluationDate);

    GoalHealthEvaluationDto evaluateWithDetails(Goal goal);

    GoalHealthEvaluationDto evaluateWithDetails(Goal goal, LocalDate evaluationDate);

    GoalHealthOverviewResponse getGoalHealthOverview(Long userId);

    GoalHealthEvaluationDto getGoalHealth(Long userId, Long goalId);
}
