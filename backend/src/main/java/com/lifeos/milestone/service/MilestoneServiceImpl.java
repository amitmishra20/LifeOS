package com.lifeos.milestone.service;

import com.lifeos.common.exception.ResourceNotFoundException;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.goal.service.GoalService;
import com.lifeos.milestone.dto.CreateMilestoneRequest;
import com.lifeos.milestone.dto.MilestoneResponse;
import com.lifeos.milestone.dto.UpdateMilestoneRequest;
import com.lifeos.milestone.entity.Milestone;
import com.lifeos.milestone.repository.MilestoneRepository;
import com.lifeos.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MilestoneServiceImpl implements MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final GoalRepository goalRepository;
    private final GoalService goalService;

    public MilestoneServiceImpl(
            MilestoneRepository milestoneRepository,
            GoalRepository goalRepository,
            GoalService goalService
    ) {
        this.milestoneRepository = milestoneRepository;
        this.goalRepository = goalRepository;
        this.goalService = goalService;
    }

    @Override
    @Transactional
    public MilestoneResponse createMilestone(Long userId, Long goalId, CreateMilestoneRequest request) {
        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        User user = goal.getUser();

        int orderIndex = request.getOrderIndex() != null
                ? request.getOrderIndex()
                : milestoneRepository.findTopByGoalIdAndUserIdOrderByOrderIndexDesc(goalId, userId)
                        .map(m -> m.getOrderIndex() + 1)
                        .orElse(0);

        Milestone milestone = new Milestone(goal, user, request.getTitle(), orderIndex);
        milestone.setDescription(request.getDescription());
        milestone.setTargetDate(request.getTargetDate());
        if (request.getStatus() != null) {
            milestone.setStatus(request.getStatus());
        }

        Milestone savedMilestone = milestoneRepository.save(milestone);
        goalService.recalculateProgress(goalId, userId);

        return MilestoneResponse.fromEntity(savedMilestone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MilestoneResponse> getMilestonesByGoal(Long userId, Long goalId) {
        if (!goalRepository.existsByIdAndUserId(goalId, userId)) {
            throw new ResourceNotFoundException("Goal not found");
        }

        return milestoneRepository.findByGoalIdAndUserIdOrderByOrderIndexAscCreatedAtAsc(goalId, userId)
                .stream()
                .map(MilestoneResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MilestoneResponse getMilestoneById(Long userId, Long milestoneId) {
        Milestone milestone = milestoneRepository.findByIdAndUserId(milestoneId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));

        return MilestoneResponse.fromEntity(milestone);
    }

    @Override
    @Transactional
    public MilestoneResponse updateMilestone(Long userId, Long milestoneId, UpdateMilestoneRequest request) {
        Milestone milestone = milestoneRepository.findByIdAndUserId(milestoneId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));

        milestone.setTitle(request.getTitle());
        milestone.setDescription(request.getDescription());
        milestone.setTargetDate(request.getTargetDate());
        milestone.setStatus(request.getStatus());
        if (request.getOrderIndex() != null) {
            milestone.setOrderIndex(request.getOrderIndex());
        }

        Milestone updatedMilestone = milestoneRepository.save(milestone);
        goalService.recalculateProgress(milestone.getGoal().getId(), userId);

        return MilestoneResponse.fromEntity(updatedMilestone);
    }

    @Override
    @Transactional
    public void deleteMilestone(Long userId, Long milestoneId) {
        Milestone milestone = milestoneRepository.findByIdAndUserId(milestoneId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));

        Long goalId = milestone.getGoal().getId();
        milestoneRepository.delete(milestone);
        goalService.recalculateProgress(goalId, userId);
    }
}
