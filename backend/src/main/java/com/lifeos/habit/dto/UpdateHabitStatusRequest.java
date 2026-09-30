package com.lifeos.habit.dto;

import com.lifeos.habit.entity.HabitStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateHabitStatusRequest {

    @NotNull(message = "Status is required")
    private HabitStatus status;

    public UpdateHabitStatusRequest() {
    }

    public UpdateHabitStatusRequest(HabitStatus status) {
        this.status = status;
    }

    public HabitStatus getStatus() {
        return status;
    }

    public void setStatus(HabitStatus status) {
        this.status = status;
    }
}
