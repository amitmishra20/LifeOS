package com.lifeos.habit.service;

import com.lifeos.common.exception.ResourceNotFoundException;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.habit.dto.CreateHabitRequest;
import com.lifeos.habit.dto.HabitDayHistoryDto;
import com.lifeos.habit.dto.HabitLogResponse;
import com.lifeos.habit.dto.HabitResponse;
import com.lifeos.habit.dto.HabitToggleResponse;
import com.lifeos.habit.dto.UpdateHabitRequest;
import com.lifeos.habit.dto.UpdateHabitStatusRequest;
import com.lifeos.habit.entity.Habit;
import com.lifeos.habit.entity.HabitFrequencyType;
import com.lifeos.habit.entity.HabitLog;
import com.lifeos.habit.entity.HabitPauseInterval;
import com.lifeos.habit.entity.HabitStatus;
import com.lifeos.habit.repository.HabitLogRepository;
import com.lifeos.habit.repository.HabitPauseIntervalRepository;
import com.lifeos.habit.repository.HabitRepository;
import com.lifeos.user.entity.User;
import com.lifeos.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final HabitPauseIntervalRepository pauseIntervalRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private Clock clock = Clock.systemDefaultZone();

    public HabitServiceImpl(
            HabitRepository habitRepository,
            HabitLogRepository habitLogRepository,
            HabitPauseIntervalRepository pauseIntervalRepository,
            UserRepository userRepository,
            GoalRepository goalRepository
    ) {
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
        this.pauseIntervalRepository = pauseIntervalRepository;
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Override
    @Transactional
    public HabitResponse createHabit(Long userId, CreateHabitRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Goal goal = null;
        if (request.getGoalId() != null) {
            goal = goalRepository.findByIdAndUserId(request.getGoalId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        }

        Habit habit = new Habit();
        habit.setUser(user);
        habit.setTitle(request.getTitle());
        habit.setDescription(request.getDescription());
        habit.setGoal(goal);
        habit.setIcon(request.getIcon());
        habit.setStatus(HabitStatus.ACTIVE);

        validateAndNormalizeFrequency(habit, request.getFrequencyType(), request.getTargetDaysMask(), request.getTargetPerWeek());

        Habit savedHabit = habitRepository.save(habit);
        return mapToHabitResponse(savedHabit, LocalDate.now(clock));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitResponse> getHabits(Long userId, HabitStatus status) {
        List<Habit> habits;
        if (status != null) {
            habits = habitRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
        } else {
            habits = habitRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        LocalDate today = LocalDate.now(clock);
        return habits.stream()
                .map(h -> mapToHabitResponse(h, today))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HabitResponse getHabitById(Long userId, Long id) {
        Habit habit = habitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Habit not found"));
        return mapToHabitResponse(habit, LocalDate.now(clock));
    }

    @Override
    @Transactional
    public HabitResponse updateHabit(Long userId, Long id, UpdateHabitRequest request) {
        Habit habit = habitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Habit not found"));

        Goal goal = null;
        if (request.getGoalId() != null) {
            goal = goalRepository.findByIdAndUserId(request.getGoalId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        }

        habit.setTitle(request.getTitle());
        habit.setDescription(request.getDescription());
        habit.setGoal(goal);
        habit.setIcon(request.getIcon());

        validateAndNormalizeFrequency(habit, request.getFrequencyType(), request.getTargetDaysMask(), request.getTargetPerWeek());

        Habit savedHabit = habitRepository.save(habit);
        return mapToHabitResponse(savedHabit, LocalDate.now(clock));
    }

    @Override
    @Transactional
    public HabitResponse updateHabitStatus(Long userId, Long id, UpdateHabitStatusRequest request) {
        Habit habit = habitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Habit not found"));

        HabitStatus currentStatus = habit.getStatus();
        HabitStatus targetStatus = request.getStatus();

        if (targetStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        if (currentStatus == targetStatus) {
            throw new IllegalArgumentException("Cannot transition habit from " + currentStatus + " to " + targetStatus);
        }

        boolean isAllowed = switch (currentStatus) {
            case ACTIVE -> targetStatus == HabitStatus.PAUSED || targetStatus == HabitStatus.ARCHIVED;
            case PAUSED -> targetStatus == HabitStatus.ACTIVE || targetStatus == HabitStatus.ARCHIVED;
            case ARCHIVED -> targetStatus == HabitStatus.ACTIVE;
        };

        if (!isAllowed) {
            throw new IllegalArgumentException("Invalid status transition from " + currentStatus + " to " + targetStatus);
        }

        Instant now = Instant.now(clock);

        if (currentStatus == HabitStatus.ACTIVE && targetStatus == HabitStatus.PAUSED) {
            habit.setStatus(HabitStatus.PAUSED);
            habit.setPausedAt(now);
            HabitPauseInterval pauseInterval = new HabitPauseInterval(habit, habit.getUser(), now);
            pauseIntervalRepository.save(pauseInterval);
        } else if (currentStatus == HabitStatus.PAUSED && targetStatus == HabitStatus.ACTIVE) {
            habit.setStatus(HabitStatus.ACTIVE);
            habit.setPausedAt(null);
            pauseIntervalRepository.findFirstByHabitIdAndResumedAtIsNullOrderByPausedAtDesc(habit.getId())
                    .ifPresent(interval -> {
                        interval.setResumedAt(now);
                        pauseIntervalRepository.save(interval);
                    });
        } else if (currentStatus == HabitStatus.PAUSED && targetStatus == HabitStatus.ARCHIVED) {
            habit.setStatus(HabitStatus.ARCHIVED);
            habit.setPausedAt(null);
            pauseIntervalRepository.findFirstByHabitIdAndResumedAtIsNullOrderByPausedAtDesc(habit.getId())
                    .ifPresent(interval -> {
                        interval.setResumedAt(now);
                        pauseIntervalRepository.save(interval);
                    });
        } else if (currentStatus == HabitStatus.ACTIVE && targetStatus == HabitStatus.ARCHIVED) {
            habit.setStatus(HabitStatus.ARCHIVED);
            habit.setPausedAt(null);
        } else if (currentStatus == HabitStatus.ARCHIVED && targetStatus == HabitStatus.ACTIVE) {
            habit.setStatus(HabitStatus.ACTIVE);
            habit.setPausedAt(null);
        }

        Habit savedHabit = habitRepository.save(habit);
        return mapToHabitResponse(savedHabit, LocalDate.now(clock));
    }

    @Override
    @Transactional
    public void deleteHabit(Long userId, Long id) {
        Habit habit = habitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Habit not found"));
        habitRepository.delete(habit);
    }

    @Override
    @Transactional
    public HabitToggleResponse toggleHabit(Long userId, Long id, LocalDate date) {
        Habit habit = habitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Habit not found"));

        if (habit.getStatus() == HabitStatus.PAUSED) {
            throw new IllegalArgumentException("Cannot log completion for a paused habit. Resume habit first.");
        }
        if (habit.getStatus() == HabitStatus.ARCHIVED) {
            throw new IllegalArgumentException("Cannot log completion for an archived habit. Reactivate habit first.");
        }

        LocalDate today = LocalDate.now(clock);
        LocalDate targetDate = date != null ? date : today;

        if (targetDate.isAfter(today)) {
            throw new IllegalArgumentException("Cannot log completion for a future date.");
        }

        Optional<HabitLog> existingLog = habitLogRepository.findByHabitIdAndCompletionDate(habit.getId(), targetDate);
        boolean completed;
        if (existingLog.isPresent()) {
            habitLogRepository.delete(existingLog.get());
            habitLogRepository.flush();
            completed = false;
        } else {
            HabitLog newLog = new HabitLog(habit, habit.getUser(), targetDate);
            habitLogRepository.saveAndFlush(newLog);
            completed = true;
        }

        HabitResponse habitResponse = mapToHabitResponse(habit, today);
        return new HabitToggleResponse(
                habit.getId(),
                targetDate,
                completed,
                habitResponse.getCurrentStreak(),
                habitResponse.getLongestStreak(),
                habitResponse.getConsistencyRate(),
                habitResponse.getWeeklyTargetProgress(),
                habitResponse.getWeeklyTargetRemaining(),
                habitResponse
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitDayHistoryDto> getHabitHistory(Long userId, Long id) {
        Habit habit = habitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Habit not found"));
        return buildHistory(habit, LocalDate.now(clock));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitResponse> getTodayHabits(Long userId) {
        List<Habit> activeHabits = habitRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, HabitStatus.ACTIVE);
        LocalDate today = LocalDate.now(clock);
        return activeHabits.stream()
                .map(h -> mapToHabitResponse(h, today))
                .collect(Collectors.toList());
    }

    // =========================================================================
    // Validation & Metric Calculation Helpers
    // =========================================================================

    private void validateAndNormalizeFrequency(Habit habit, HabitFrequencyType frequencyType, String targetDaysMask, Integer targetPerWeek) {
        if (frequencyType == null) {
            throw new IllegalArgumentException("Frequency type is required");
        }
        habit.setFrequencyType(frequencyType);
        switch (frequencyType) {
            case DAILY -> {
                habit.setTargetDaysMask(null);
                habit.setTargetPerWeek(7);
            }
            case SPECIFIC_DAYS -> {
                if (targetDaysMask == null || targetDaysMask.trim().isEmpty()) {
                    throw new IllegalArgumentException("Target days mask is required for SPECIFIC_DAYS habit");
                }
                Set<Integer> days = parseAndValidateDaysMask(targetDaysMask);
                if (days.isEmpty()) {
                    throw new IllegalArgumentException("At least one target day must be selected");
                }
                String normalizedMask = days.stream()
                        .sorted()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                habit.setTargetDaysMask(normalizedMask);
                habit.setTargetPerWeek(days.size());
            }
            case WEEKLY_TARGET -> {
                if (targetPerWeek == null || targetPerWeek < 1 || targetPerWeek > 7) {
                    throw new IllegalArgumentException("Weekly target must be between 1 and 7");
                }
                habit.setTargetDaysMask(null);
                habit.setTargetPerWeek(targetPerWeek);
            }
        }
    }

    private Set<Integer> parseAndValidateDaysMask(String mask) {
        Set<Integer> days = new HashSet<>();
        String[] parts = mask.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) continue;
            try {
                int day = Integer.parseInt(trimmed);
                if (day < 1 || day > 7) {
                    throw new IllegalArgumentException("Day must be between 1 (Monday) and 7 (Sunday)");
                }
                days.add(day);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid day value in mask: " + trimmed);
            }
        }
        return days;
    }

    private Set<Integer> parseDaysMask(String mask) {
        if (mask == null || mask.trim().isEmpty()) {
            return Collections.emptySet();
        }
        Set<Integer> days = new HashSet<>();
        for (String part : mask.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                try {
                    days.add(Integer.parseInt(trimmed));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return days;
    }

    private boolean isDatePaused(LocalDate date, List<HabitPauseInterval> pauseIntervals, ZoneId zoneId) {
        for (HabitPauseInterval interval : pauseIntervals) {
            LocalDate pauseStart = interval.getPausedAt().atZone(zoneId).toLocalDate();
            if (interval.getResumedAt() != null) {
                LocalDate resumeDate = interval.getResumedAt().atZone(zoneId).toLocalDate();
                if (!date.isBefore(pauseStart) && date.isBefore(resumeDate)) {
                    return true;
                }
            } else {
                if (!date.isBefore(pauseStart)) {
                    return true;
                }
            }
        }
        return false;
    }

    private HabitResponse mapToHabitResponse(Habit habit, LocalDate today) {
        HabitResponse response = new HabitResponse();
        response.setId(habit.getId());
        response.setUserId(habit.getUser().getId());
        if (habit.getGoal() != null) {
            response.setGoalId(habit.getGoal().getId());
            response.setGoalTitle(habit.getGoal().getTitle());
        }
        response.setTitle(habit.getTitle());
        response.setDescription(habit.getDescription());
        response.setFrequencyType(habit.getFrequencyType());
        response.setTargetDaysMask(habit.getTargetDaysMask());
        response.setTargetPerWeek(habit.getTargetPerWeek());
        response.setStatus(habit.getStatus());
        response.setPausedAt(habit.getPausedAt());
        response.setIcon(habit.getIcon());
        response.setCreatedAt(habit.getCreatedAt());
        response.setUpdatedAt(habit.getUpdatedAt());

        List<HabitLog> logs = habitLogRepository.findByHabitIdOrderByCompletionDateAsc(habit.getId());
        List<HabitPauseInterval> pauseIntervals = pauseIntervalRepository.findByHabitIdOrderByPausedAtAsc(habit.getId());
        ZoneId zoneId = ZoneId.systemDefault();
        Set<LocalDate> completedDates = logs.stream().map(HabitLog::getCompletionDate).collect(Collectors.toSet());

        response.setCompletedToday(completedDates.contains(today));

        LocalDate habitCreatedDate = habit.getCreatedAt() != null
                ? habit.getCreatedAt().atZone(zoneId).toLocalDate()
                : today;
        if (habitCreatedDate.isAfter(today)) {
            habitCreatedDate = today;
        }

        // Metric & streak calculation
        calculateMetricsAndStreaks(habit, response, logs, pauseIntervals, completedDates, habitCreatedDate, today, zoneId);

        // 7-day history
        response.setHistory(buildHistory(habit, today, logs, pauseIntervals, completedDates, habitCreatedDate, zoneId));

        return response;
    }

    private void calculateMetricsAndStreaks(
            Habit habit,
            HabitResponse response,
            List<HabitLog> logs,
            List<HabitPauseInterval> pauseIntervals,
            Set<LocalDate> completedDates,
            LocalDate habitCreatedDate,
            LocalDate today,
            ZoneId zoneId
    ) {
        HabitFrequencyType freq = habit.getFrequencyType();

        if (freq == HabitFrequencyType.DAILY) {
            // Consistency (30-day window: max(habitCreatedDate, today - 29) to today)
            LocalDate windowStart = habitCreatedDate.isAfter(today.minusDays(29)) ? habitCreatedDate : today.minusDays(29);
            int denominator = 0;
            int numerator = 0;
            LocalDate d = windowStart;
            while (!d.isAfter(today)) {
                if (!isDatePaused(d, pauseIntervals, zoneId)) {
                    denominator++;
                    if (completedDates.contains(d)) {
                        numerator++;
                    }
                }
                d = d.plusDays(1);
            }
            int rate = denominator == 0 ? 0 : Math.min(100, (int) Math.round(((double) numerator / denominator) * 100.0));
            response.setConsistencyRate(rate);

            // Streak: start from today, or yesterday if today has not yet been completed
            LocalDate cursor = today;
            if (!completedDates.contains(today)) {
                cursor = today.minusDays(1);
            }
            int currentStreak = 0;
            while (!cursor.isBefore(habitCreatedDate)) {
                if (isDatePaused(cursor, pauseIntervals, zoneId)) {
                    cursor = cursor.minusDays(1);
                    continue;
                }
                if (completedDates.contains(cursor)) {
                    currentStreak++;
                    cursor = cursor.minusDays(1);
                } else {
                    break;
                }
            }
            response.setCurrentStreak(currentStreak);

            // Longest streak
            int longestStreak = 0;
            int tempStreak = 0;
            d = habitCreatedDate;
            while (!d.isAfter(today)) {
                if (isDatePaused(d, pauseIntervals, zoneId)) {
                    d = d.plusDays(1);
                    continue;
                }
                if (completedDates.contains(d)) {
                    tempStreak++;
                    if (tempStreak > longestStreak) {
                        longestStreak = tempStreak;
                    }
                } else {
                    tempStreak = 0;
                }
                d = d.plusDays(1);
            }
            if (currentStreak > longestStreak) {
                longestStreak = currentStreak;
            }
            response.setLongestStreak(longestStreak);
            response.setWeeklyTargetProgress(null);
            response.setWeeklyTargetRemaining(null);

        } else if (freq == HabitFrequencyType.SPECIFIC_DAYS) {
            Set<Integer> targetDays = parseDaysMask(habit.getTargetDaysMask());
            LocalDate windowStart = habitCreatedDate.isAfter(today.minusDays(29)) ? habitCreatedDate : today.minusDays(29);
            int denominator = 0;
            int numerator = 0;
            LocalDate d = windowStart;
            while (!d.isAfter(today)) {
                if (targetDays.contains(d.getDayOfWeek().getValue()) && !isDatePaused(d, pauseIntervals, zoneId)) {
                    denominator++;
                    if (completedDates.contains(d)) {
                        numerator++;
                    }
                }
                d = d.plusDays(1);
            }
            int rate = denominator == 0 ? 0 : Math.min(100, (int) Math.round(((double) numerator / denominator) * 100.0));
            response.setConsistencyRate(rate);

            // Streak: start from most recent scheduled elapsed date
            LocalDate cursor = today;
            if (targetDays.contains(today.getDayOfWeek().getValue()) && completedDates.contains(today)) {
                cursor = today;
            } else {
                cursor = today.minusDays(1);
                while (!cursor.isBefore(habitCreatedDate) && !targetDays.contains(cursor.getDayOfWeek().getValue())) {
                    cursor = cursor.minusDays(1);
                }
            }
            int currentStreak = 0;
            while (!cursor.isBefore(habitCreatedDate)) {
                if (!targetDays.contains(cursor.getDayOfWeek().getValue())) {
                    cursor = cursor.minusDays(1);
                    continue;
                }
                if (isDatePaused(cursor, pauseIntervals, zoneId)) {
                    cursor = cursor.minusDays(1);
                    continue;
                }
                if (completedDates.contains(cursor)) {
                    currentStreak++;
                    cursor = cursor.minusDays(1);
                } else {
                    break;
                }
            }
            response.setCurrentStreak(currentStreak);

            // Longest streak
            int longestStreak = 0;
            int tempStreak = 0;
            d = habitCreatedDate;
            while (!d.isAfter(today)) {
                if (!targetDays.contains(d.getDayOfWeek().getValue())) {
                    d = d.plusDays(1);
                    continue;
                }
                if (isDatePaused(d, pauseIntervals, zoneId)) {
                    d = d.plusDays(1);
                    continue;
                }
                if (completedDates.contains(d)) {
                    tempStreak++;
                    if (tempStreak > longestStreak) {
                        longestStreak = tempStreak;
                    }
                } else {
                    tempStreak = 0;
                }
                d = d.plusDays(1);
            }
            if (currentStreak > longestStreak) {
                longestStreak = currentStreak;
            }
            response.setLongestStreak(longestStreak);
            response.setWeeklyTargetProgress(null);
            response.setWeeklyTargetRemaining(null);

        } else if (freq == HabitFrequencyType.WEEKLY_TARGET) {
            LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            long completionsThisWeek = 0;
            LocalDate d = monday;
            while (!d.isAfter(today)) {
                if (completedDates.contains(d)) {
                    completionsThisWeek++;
                }
                d = d.plusDays(1);
            }
            int target = habit.getTargetPerWeek() != null && habit.getTargetPerWeek() > 0 ? habit.getTargetPerWeek() : 1;
            int remaining = Math.max(0, target - (int) completionsThisWeek);
            int consistencyRate = Math.min(100, (int) Math.round(((double) completionsThisWeek / target) * 100.0));

            response.setConsistencyRate(consistencyRate);
            response.setCurrentStreak(null);
            response.setLongestStreak(null);
            response.setWeeklyTargetProgress((int) completionsThisWeek);
            response.setWeeklyTargetRemaining(remaining);
        }
    }

    private List<HabitDayHistoryDto> buildHistory(Habit habit, LocalDate today) {
        List<HabitLog> logs = habitLogRepository.findByHabitIdOrderByCompletionDateAsc(habit.getId());
        List<HabitPauseInterval> pauseIntervals = pauseIntervalRepository.findByHabitIdOrderByPausedAtAsc(habit.getId());
        ZoneId zoneId = ZoneId.systemDefault();
        Set<LocalDate> completedDates = logs.stream().map(HabitLog::getCompletionDate).collect(Collectors.toSet());
        LocalDate habitCreatedDate = habit.getCreatedAt() != null
                ? habit.getCreatedAt().atZone(zoneId).toLocalDate()
                : today;
        if (habitCreatedDate.isAfter(today)) {
            habitCreatedDate = today;
        }
        return buildHistory(habit, today, logs, pauseIntervals, completedDates, habitCreatedDate, zoneId);
    }

    private List<HabitDayHistoryDto> buildHistory(
            Habit habit,
            LocalDate today,
            List<HabitLog> logs,
            List<HabitPauseInterval> pauseIntervals,
            Set<LocalDate> completedDates,
            LocalDate habitCreatedDate,
            ZoneId zoneId
    ) {
        List<HabitDayHistoryDto> history = new ArrayList<>();
        Set<Integer> targetDays = habit.getFrequencyType() == HabitFrequencyType.SPECIFIC_DAYS
                ? parseDaysMask(habit.getTargetDaysMask())
                : Collections.emptySet();

        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            boolean isPaused = isDatePaused(d, pauseIntervals, zoneId);
            boolean isCompleted = completedDates.contains(d);
            boolean isToday = d.equals(today);
            String dayOfWeek = d.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            boolean isScheduled = false;
            if (!isPaused && !d.isBefore(habitCreatedDate) && !d.isAfter(today)) {
                if (habit.getFrequencyType() == HabitFrequencyType.DAILY) {
                    isScheduled = true;
                } else if (habit.getFrequencyType() == HabitFrequencyType.SPECIFIC_DAYS) {
                    isScheduled = targetDays.contains(d.getDayOfWeek().getValue());
                } else {
                    // WEEKLY_TARGET: flexible, isScheduled is false
                    isScheduled = false;
                }
            }

            history.add(new HabitDayHistoryDto(d, dayOfWeek, isScheduled, isCompleted, isPaused, isToday));
        }

        return history;
    }
}
