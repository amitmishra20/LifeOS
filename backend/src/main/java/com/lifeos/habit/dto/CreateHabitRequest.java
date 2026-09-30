package com.lifeos.habit.dto;

import com.lifeos.habit.entity.HabitFrequencyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateHabitRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    @NotNull(message = "Frequency type is required")
    private HabitFrequencyType frequencyType;

    private String targetDaysMask;

    private Integer targetPerWeek;

    private Long goalId;

    private String icon;

    public CreateHabitRequest() {
    }

    public CreateHabitRequest(String title, HabitFrequencyType frequencyType, Integer targetPerWeek) {
        this.title = title;
        this.frequencyType = frequencyType;
        this.targetPerWeek = targetPerWeek;
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

    public HabitFrequencyType getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(HabitFrequencyType frequencyType) {
        this.frequencyType = frequencyType;
    }

    public String getTargetDaysMask() {
        return targetDaysMask;
    }

    public void setTargetDaysMask(String targetDaysMask) {
        this.targetDaysMask = targetDaysMask;
    }

    public Integer getTargetPerWeek() {
        return targetPerWeek;
    }

    public void setTargetPerWeek(Integer targetPerWeek) {
        this.targetPerWeek = targetPerWeek;
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
