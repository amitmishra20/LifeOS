package com.lifeos.habit.service;

import com.lifeos.habit.dto.CreateHabitRequest;
import com.lifeos.habit.dto.HabitDayHistoryDto;
import com.lifeos.habit.dto.HabitResponse;
import com.lifeos.habit.dto.HabitToggleResponse;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class HabitServiceTest {

    @Autowired
    private HabitServiceImpl habitService;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private HabitLogRepository habitLogRepository;

    @Autowired
    private HabitPauseIntervalRepository pauseIntervalRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private final ZoneId zoneId = ZoneId.systemDefault();

    @BeforeEach
    void setUp() {
        cleanup();
        testUser = userRepository.save(new User("Metrics User", "metrics.user@example.com", "hash123"));
        habitService.setClock(Clock.systemDefaultZone());
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    private void cleanup() {
        pauseIntervalRepository.deleteAll();
        habitLogRepository.deleteAll();
        habitRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should correctly calculate DAILY denominator and consistency rate")
    void dailyHabit_DenominatorAndConsistency() {
        // Fix clock to a specific day: 2026-01-30
        LocalDate fixedToday = LocalDate.of(2026, 1, 30);
        Clock fixedClock = Clock.fixed(fixedToday.atStartOfDay(zoneId).toInstant(), zoneId);
        habitService.setClock(fixedClock);

        Habit habit = new Habit(testUser, "Daily Walk", HabitFrequencyType.DAILY, 7);
        habit = habitRepository.save(habit);
        // Created 10 days ago (2026-01-20)
        habitRepository.updateCreatedAt(habit.getId(), LocalDate.of(2026, 1, 20).atStartOfDay(zoneId).toInstant());

        // Complete 5 of the 11 days (Jan 20 to Jan 30 is 11 days inclusive)
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 20)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 22)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 25)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 28)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 30)));

        HabitResponse response = habitService.getHabitById(testUser.getId(), habit.getId());

        // 5 / 11 = 45.45% -> 45%
        assertThat(response.getConsistencyRate()).isEqualTo(45);
    }

    @Test
    @DisplayName("Should correctly calculate DAILY streak when today completed vs uncompleted")
    void dailyHabit_StreakCalculation() {
        LocalDate fixedToday = LocalDate.of(2026, 1, 10);
        Clock fixedClock = Clock.fixed(fixedToday.atStartOfDay(zoneId).toInstant(), zoneId);
        habitService.setClock(fixedClock);

        Habit habit = new Habit(testUser, "Code", HabitFrequencyType.DAILY, 7);
        habit = habitRepository.save(habit);
        habitRepository.updateCreatedAt(habit.getId(), LocalDate.of(2026, 1, 1).atStartOfDay(zoneId).toInstant());

        // Complete Jan 7, 8, 9 (yesterday), but not Jan 10 (today)
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 7)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 8)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 9)));

        HabitResponse response = habitService.getHabitById(testUser.getId(), habit.getId());
        // Streak starts from yesterday if today not completed yet: Jan 9 (1), Jan 8 (2), Jan 7 (3) -> 3
        assertThat(response.getCurrentStreak()).isEqualTo(3);

        // Now toggle today
        HabitToggleResponse toggleResponse = habitService.toggleHabit(testUser.getId(), habit.getId(), fixedToday);
        assertThat(toggleResponse.isCompleted()).isTrue();
        assertThat(toggleResponse.getCurrentStreak()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should correctly calculate SPECIFIC_DAYS denominator and streak")
    void specificDaysHabit_DenominatorAndStreak() {
        // Suppose today is Friday 2026-01-16 (1=Mon, 3=Wed, 5=Fri)
        LocalDate fixedToday = LocalDate.of(2026, 1, 16); // Friday
        Clock fixedClock = Clock.fixed(fixedToday.atStartOfDay(zoneId).toInstant(), zoneId);
        habitService.setClock(fixedClock);

        Habit habit = new Habit(testUser, "Gym MWF", HabitFrequencyType.SPECIFIC_DAYS, 3);
        habit.setTargetDaysMask("1,3,5");
        habit = habitRepository.save(habit);
        // Created Monday 2026-01-05
        habitRepository.updateCreatedAt(habit.getId(), LocalDate.of(2026, 1, 5).atStartOfDay(zoneId).toInstant());

        // Scheduled days in [Jan 5, Jan 16]:
        // Week 1: Mon Jan 5, Wed Jan 7, Fri Jan 9 (3 days)
        // Week 2: Mon Jan 12, Wed Jan 14, Fri Jan 16 (3 days)
        // Total eligible denominator: 6 days.

        // Complete Mon Jan 12, Wed Jan 14, Fri Jan 16
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 12)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 14)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 16)));

        HabitResponse response = habitService.getHabitById(testUser.getId(), habit.getId());

        // 3 completed out of 6 eligible scheduled days = 50%
        assertThat(response.getConsistencyRate()).isEqualTo(50);
        // Current streak: Fri 16, Wed 14, Mon 12 = 3. Friday Jan 9 was uncompleted -> breaks streak.
        assertThat(response.getCurrentStreak()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should correctly handle WEEKLY_TARGET metrics: remaining, capped rate, and null streak")
    void weeklyTargetHabit_Metrics() {
        // Today is Thursday 2026-01-15 (Monday was Jan 12)
        LocalDate fixedToday = LocalDate.of(2026, 1, 15);
        Clock fixedClock = Clock.fixed(fixedToday.atStartOfDay(zoneId).toInstant(), zoneId);
        habitService.setClock(fixedClock);

        Habit habit = new Habit(testUser, "Run 3x/week", HabitFrequencyType.WEEKLY_TARGET, 3);
        habit = habitRepository.save(habit);
        habitRepository.updateCreatedAt(habit.getId(), LocalDate.of(2026, 1, 1).atStartOfDay(zoneId).toInstant());

        // Complete 2 runs this week (Monday Jan 12, Wednesday Jan 14)
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 12)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 14)));

        HabitResponse response = habitService.getHabitById(testUser.getId(), habit.getId());

        assertThat(response.getCurrentStreak()).isNull();
        assertThat(response.getLongestStreak()).isNull();
        assertThat(response.getWeeklyTargetProgress()).isEqualTo(2);
        assertThat(response.getWeeklyTargetRemaining()).isEqualTo(1);
        // 2 / 3 = 66.67% -> 67%
        assertThat(response.getConsistencyRate()).isEqualTo(67);

        // Log 2 more runs this week (total 4 out of target 3)
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 15)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 11))); // prior week (should not count for this week)

        HabitResponse updatedResponse = habitService.getHabitById(testUser.getId(), habit.getId());
        assertThat(updatedResponse.getWeeklyTargetProgress()).isEqualTo(3);
        assertThat(updatedResponse.getWeeklyTargetRemaining()).isEqualTo(0);
        // Capped rate at 100%
        assertThat(updatedResponse.getConsistencyRate()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should exclude paused intervals and multiple pause/resume cycles from consistency and bypass in streak")
    void multiplePauseResumeCycles_HandledCorrectly() {
        // Scenario from specification:
        // ACTIVE -> PAUSED Jan 10 -> ACTIVE Jan 15 -> PAUSED Jan 20 -> ACTIVE Jan 25
        // Jan 10–14 and Jan 20–24 were paused intervals.
        LocalDate fixedToday = LocalDate.of(2026, 1, 28);
        Clock fixedClock = Clock.fixed(fixedToday.atStartOfDay(zoneId).toInstant(), zoneId);
        habitService.setClock(fixedClock);

        Habit habit = new Habit(testUser, "Continuous Reading", HabitFrequencyType.DAILY, 7);
        habit = habitRepository.save(habit);
        habitRepository.updateCreatedAt(habit.getId(), LocalDate.of(2026, 1, 5).atStartOfDay(zoneId).toInstant());

        // Pause interval 1: Jan 10 to Jan 15
        HabitPauseInterval pause1 = new HabitPauseInterval(habit, testUser, LocalDate.of(2026, 1, 10).atStartOfDay(zoneId).toInstant());
        pause1.setResumedAt(LocalDate.of(2026, 1, 15).atStartOfDay(zoneId).toInstant());
        pauseIntervalRepository.save(pause1);

        // Pause interval 2: Jan 20 to Jan 25
        HabitPauseInterval pause2 = new HabitPauseInterval(habit, testUser, LocalDate.of(2026, 1, 20).atStartOfDay(zoneId).toInstant());
        pause2.setResumedAt(LocalDate.of(2026, 1, 25).atStartOfDay(zoneId).toInstant());
        pauseIntervalRepository.save(pause2);

        // Completed:
        // Jan 25, 26, 27, 28 (active after 2nd pause)
        // Jan 19, 18, 17, 16, 15 (active between pauses)
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 28)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 27)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 26)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 25)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 19)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 18)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 17)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 16)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 15)));

        HabitResponse response = habitService.getHabitById(testUser.getId(), habit.getId());

        // Streak check:
        // Jan 28 (1), 27 (2), 26 (3), 25 (4)
        // Jan 24, 23, 22, 21, 20 are PAUSED -> bypassed!
        // Jan 19 (5), 18 (6), 17 (7), 16 (8), 15 (9)
        // Jan 14, 13, 12, 11, 10 are PAUSED -> bypassed!
        // Jan 9 was uncompleted -> breaks streak!
        // Streak must be 9!
        assertThat(response.getCurrentStreak()).isEqualTo(9);

        // Denominator check:
        // Window is Jan 5 to Jan 28 (24 calendar days).
        // Paused days: Jan 10,11,12,13,14 (5 days) + Jan 20,21,22,23,24 (5 days) = 10 paused days.
        // Eligible denominator = 24 - 10 = 14 days.
        // Completed = 9 logs on eligible days.
        // Consistency rate = round(9 / 14 * 100) = round(64.28%) = 64%.
        assertThat(response.getConsistencyRate()).isEqualTo(64);
    }

    @Test
    @DisplayName("Should include correct scheduling metadata in 7-day history")
    void historySchedulingMetadata() {
        LocalDate fixedToday = LocalDate.of(2026, 1, 20); // Tuesday (ISO day 2)
        Clock fixedClock = Clock.fixed(fixedToday.atStartOfDay(zoneId).toInstant(), zoneId);
        habitService.setClock(fixedClock);

        Habit habit = new Habit(testUser, "MWF Habit", HabitFrequencyType.SPECIFIC_DAYS, 3);
        habit.setTargetDaysMask("1,3,5"); // Mon (1), Wed (3), Fri (5)
        habit = habitRepository.save(habit);
        habitRepository.updateCreatedAt(habit.getId(), LocalDate.of(2026, 1, 1).atStartOfDay(zoneId).toInstant());

        List<HabitDayHistoryDto> history = habitService.getHabitHistory(testUser.getId(), habit.getId());
        assertThat(history).hasSize(7);

        // Check each day's isScheduled matches 1,3,5
        for (HabitDayHistoryDto day : history) {
            int dayVal = day.getDate().getDayOfWeek().getValue();
            if (dayVal == 1 || dayVal == 3 || dayVal == 5) {
                assertThat(day.isScheduled()).isTrue();
            } else {
                assertThat(day.isScheduled()).isFalse();
            }
        }
    }

    @Test
    @DisplayName("Database should prevent duplicate completion on same habit and date")
    void databaseDuplicateCompletionProtection() {
        Habit habit = habitRepository.save(new Habit(testUser, "Unique Test", HabitFrequencyType.DAILY, 7));
        LocalDate date = LocalDate.of(2026, 1, 15);

        habitLogRepository.save(new HabitLog(habit, testUser, date));

        assertThatThrownBy(() -> {
            habitLogRepository.saveAndFlush(new HabitLog(habit, testUser, date));
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Deleting habit should cascade delete all associated logs and pause intervals")
    void deleteHabitCascadesLogsAndPauses() {
        Habit habit = habitRepository.save(new Habit(testUser, "Cascade Test", HabitFrequencyType.DAILY, 7));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 15)));
        habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.of(2026, 1, 16)));
        pauseIntervalRepository.save(new HabitPauseInterval(habit, testUser, Instant.now()));

        habitService.deleteHabit(testUser.getId(), habit.getId());

        assertThat(habitRepository.findById(habit.getId())).isEmpty();
        assertThat(habitLogRepository.findByHabitIdOrderByCompletionDateAsc(habit.getId())).isEmpty();
        assertThat(pauseIntervalRepository.findByHabitIdOrderByPausedAtAsc(habit.getId())).isEmpty();
    }
}
