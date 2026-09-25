package com.lifeos.goal.dto;

import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.entity.GoalPriority;
import com.lifeos.goal.entity.GoalStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UpdateGoalRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Category is required")
    private GoalCategory category;

    @NotNull(message = "Priority is required")
    private GoalPriority priority;

    @NotNull(message = "Status is required")
    private GoalStatus status;

    private LocalDate startDate;

    private LocalDate targetDate;

    public UpdateGoalRequest() {
    }

    @AssertTrue(message = "Target date must be on or after start date")
    public boolean isValidDateRange() {
        if (startDate != null && targetDate != null) {
            return !targetDate.isBefore(startDate);
        }
        return true;
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

    public GoalCategory getCategory() {
        return category;
    }

    public void setCategory(GoalCategory category) {
        this.category = category;
    }

    public GoalPriority getPriority() {
        return priority;
    }

    public void setPriority(GoalPriority priority) {
        this.priority = priority;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }
}
