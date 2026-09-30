package com.lifeos.learning.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateLearningProgressRequest {

    @NotNull(message = "Current progress is required")
    @Min(value = 0, message = "Current progress must be between 0 and 100")
    @Max(value = 100, message = "Current progress must be between 0 and 100")
    private Integer currentProgress;

    public UpdateLearningProgressRequest() {
    }

    public UpdateLearningProgressRequest(Integer currentProgress) {
        this.currentProgress = currentProgress;
    }

    public Integer getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(Integer currentProgress) {
        this.currentProgress = currentProgress;
    }
}
