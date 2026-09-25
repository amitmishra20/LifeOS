package com.lifeos.goal.service;

import com.lifeos.common.exception.ResourceNotFoundException;
import com.lifeos.goal.dto.CreateGoalRequest;
import com.lifeos.goal.dto.GoalDetailResponse;
import com.lifeos.goal.dto.GoalResponse;
import com.lifeos.goal.dto.UpdateGoalRequest;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalStatus;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.milestone.dto.MilestoneResponse;
import com.lifeos.milestone.entity.MilestoneStatus;
import com.lifeos.milestone.repository.MilestoneRepository;
import com.lifeos.user.entity.User;
import com.lifeos.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final MilestoneRepository milestoneRepository;
    private final UserRepository userRepository;

    public GoalServiceImpl(
            GoalRepository goalRepository,
            MilestoneRepository milestoneRepository,
            UserRepository userRepository
    ) {
        this.goalRepository = goalRepository;
        this.milestoneRepository = milestoneRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public GoalResponse createGoal(Long userId, CreateGoalRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Goal goal = new Goal(user, request.getTitle(), request.getCategory());
        goal.setDescription(request.getDescription());
        if (request.getPriority() != null) {
            goal.setPriority(request.getPriority());
        }
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setProgress(0);
        goal.setStartDate(request.getStartDate());
        goal.setTargetDate(request.getTargetDate());

        Goal savedGoal = goalRepository.save(goal);
        return GoalResponse.fromEntity(savedGoal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalResponse> getGoals(Long userId, GoalStatus status) {
        List<Goal> goals = (status != null)
                ? goalRepository.findByUserIdAndStatus(userId, status)
                : goalRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return goals.stream().map(GoalResponse::fromEntity).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GoalDetailResponse getGoalById(Long userId, Long goalId) {
        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        List<MilestoneResponse> milestoneResponses = milestoneRepository
                .findByGoalIdAndUserIdOrderByOrderIndexAscCreatedAtAsc(goalId, userId)
                .stream()
                .map(MilestoneResponse::fromEntity)
                .toList();

        return GoalDetailResponse.fromEntityWithMilestones(goal, milestoneResponses);
    }

    @Override
    @Transactional
    public GoalResponse updateGoal(Long userId, Long goalId, UpdateGoalRequest request) {
        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        goal.setTitle(request.getTitle());
        goal.setDescription(request.getDescription());
        goal.setCategory(request.getCategory());
        goal.setPriority(request.getPriority());
        goal.setStatus(request.getStatus());
        goal.setStartDate(request.getStartDate());
        goal.setTargetDate(request.getTargetDate());

        Goal updatedGoal = goalRepository.save(goal);
        return GoalResponse.fromEntity(updatedGoal);
    }

    @Override
    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        goalRepository.delete(goal);
    }

    @Override
    @Transactional
    public void recalculateProgress(Long goalId, Long userId) {
        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        long totalMilestones = milestoneRepository.countByGoalIdAndUserId(goalId, userId);
        int calculatedProgress = 0;

        if (totalMilestones > 0) {
            long completedMilestones = milestoneRepository.countByGoalIdAndUserIdAndStatus(
                    goalId, userId, MilestoneStatus.COMPLETED
            );
            calculatedProgress = (int) Math.round(((double) completedMilestones / totalMilestones) * 100);
        }

        goal.setProgress(calculatedProgress);
        // Important: Goal status is NOT automatically modified here, even if progress reaches 100%
        goalRepository.save(goal);
    }
}
