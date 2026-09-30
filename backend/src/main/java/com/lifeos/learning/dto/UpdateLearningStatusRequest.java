package com.lifeos.learning.dto;

import com.lifeos.learning.entity.LearningStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateLearningStatusRequest {

    @NotNull(message = "Status is required")
    private LearningStatus status;

    public UpdateLearningStatusRequest() {
    }

    public UpdateLearningStatusRequest(LearningStatus status) {
        this.status = status;
    }

    public LearningStatus getStatus() {
        return status;
    }

    public void setStatus(LearningStatus status) {
        this.status = status;
    }
}
