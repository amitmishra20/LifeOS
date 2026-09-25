package com.lifeos.goal.service;

import com.lifeos.goal.dto.CreateGoalRequest;
import com.lifeos.goal.dto.GoalDetailResponse;
import com.lifeos.goal.dto.GoalResponse;
import com.lifeos.goal.dto.UpdateGoalRequest;
import com.lifeos.goal.entity.GoalStatus;

import java.util.List;

public interface GoalService {

    GoalResponse createGoal(Long userId, CreateGoalRequest request);

    List<GoalResponse> getGoals(Long userId, GoalStatus status);

    GoalDetailResponse getGoalById(Long userId, Long goalId);

    GoalResponse updateGoal(Long userId, Long goalId, UpdateGoalRequest request);

    void deleteGoal(Long userId, Long goalId);

    void recalculateProgress(Long goalId, Long userId);
}
