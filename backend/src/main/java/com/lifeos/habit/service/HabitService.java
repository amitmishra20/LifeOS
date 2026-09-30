package com.lifeos.habit.service;

import com.lifeos.habit.dto.CreateHabitRequest;
import com.lifeos.habit.dto.HabitDayHistoryDto;
import com.lifeos.habit.dto.HabitResponse;
import com.lifeos.habit.dto.HabitToggleResponse;
import com.lifeos.habit.dto.UpdateHabitRequest;
import com.lifeos.habit.dto.UpdateHabitStatusRequest;
import com.lifeos.habit.entity.HabitStatus;

import java.time.LocalDate;
import java.util.List;

public interface HabitService {

    HabitResponse createHabit(Long userId, CreateHabitRequest request);

    List<HabitResponse> getHabits(Long userId, HabitStatus status);

    HabitResponse getHabitById(Long userId, Long id);

    HabitResponse updateHabit(Long userId, Long id, UpdateHabitRequest request);

    HabitResponse updateHabitStatus(Long userId, Long id, UpdateHabitStatusRequest request);

    void deleteHabit(Long userId, Long id);

    HabitToggleResponse toggleHabit(Long userId, Long id, LocalDate date);

    List<HabitDayHistoryDto> getHabitHistory(Long userId, Long id);

    List<HabitResponse> getTodayHabits(Long userId);
}
