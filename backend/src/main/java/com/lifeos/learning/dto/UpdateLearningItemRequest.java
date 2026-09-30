package com.lifeos.learning.dto;

import com.lifeos.learning.entity.LearningCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateLearningItemRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 120, message = "Title must not exceed 120 characters")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private LearningCategory category;

    @Min(value = 1, message = "Target progress must be between 1 and 100")
    @Max(value = 100, message = "Target progress must be between 1 and 100")
    private Integer targetProgress;

    private Long goalId;

    public UpdateLearningItemRequest() {
    }

    public UpdateLearningItemRequest(String title, LearningCategory category, Integer targetProgress) {
        this.title = title;
        this.category = category;
        this.targetProgress = targetProgress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LearningCategory getCategory() {
        return category;
    }

    public void setCategory(LearningCategory category) {
        this.category = category;
    }

    public Integer getTargetProgress() {
        return targetProgress;
    }

    public void setTargetProgress(Integer targetProgress) {
        this.targetProgress = targetProgress;
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }
}
