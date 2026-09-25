package com.lifeos.milestone.service;

import com.lifeos.milestone.dto.CreateMilestoneRequest;
import com.lifeos.milestone.dto.MilestoneResponse;
import com.lifeos.milestone.dto.UpdateMilestoneRequest;

import java.util.List;

public interface MilestoneService {

    MilestoneResponse createMilestone(Long userId, Long goalId, CreateMilestoneRequest request);

    List<MilestoneResponse> getMilestonesByGoal(Long userId, Long goalId);

    MilestoneResponse getMilestoneById(Long userId, Long milestoneId);

    MilestoneResponse updateMilestone(Long userId, Long milestoneId, UpdateMilestoneRequest request);

    void deleteMilestone(Long userId, Long milestoneId);
}
