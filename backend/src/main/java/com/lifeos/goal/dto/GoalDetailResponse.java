package com.lifeos.goal.dto;

import com.lifeos.goal.entity.Goal;
import com.lifeos.milestone.dto.MilestoneResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GoalDetailResponse extends GoalResponse {

    private List<MilestoneResponse> milestones = Collections.emptyList();

    public GoalDetailResponse() {
    }

    public static GoalDetailResponse fromEntityWithMilestones(Goal goal, List<MilestoneResponse> milestones) {
        GoalDetailResponse response = new GoalDetailResponse();
        response.setId(goal.getId());
        response.setTitle(goal.getTitle());
        response.setDescription(goal.getDescription());
        response.setCategory(goal.getCategory());
        response.setPriority(goal.getPriority());
        response.setStatus(goal.getStatus());
        response.setProgress(goal.getProgress());
        response.setStartDate(goal.getStartDate());
        response.setTargetDate(goal.getTargetDate());
        response.setMilestones(milestones != null ? milestones : Collections.emptyList());
        response.setMilestoneCount(response.getMilestones().size());
        response.setCompletedMilestoneCount(
                (int) response.getMilestones().stream()
                        .filter(m -> m.getStatus() == com.lifeos.milestone.entity.MilestoneStatus.COMPLETED)
                        .count()
        );
        response.setCreatedAt(goal.getCreatedAt());
        response.setUpdatedAt(goal.getUpdatedAt());
        return response;
    }

    public List<MilestoneResponse> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<MilestoneResponse> milestones) {
        this.milestones = milestones;
    }
}
