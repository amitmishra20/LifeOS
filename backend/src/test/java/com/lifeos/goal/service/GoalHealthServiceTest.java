package com.lifeos.goal.service;

import com.lifeos.common.exception.ResourceNotFoundException;
import com.lifeos.goal.config.GoalHealthProperties;
import com.lifeos.goal.dto.GoalHealthEvaluationDto;
import com.lifeos.goal.dto.GoalHealthOverviewResponse;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.entity.GoalHealth;
import com.lifeos.goal.entity.GoalStatus;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalHealthServiceTest {

    @Mock
    private GoalRepository goalRepository;

    private GoalHealthProperties properties;
    private GoalHealthServiceImpl goalHealthService;

    private User testUser;
    private LocalDate evaluationDate;

    @BeforeEach
    void setUp() {
        properties = new GoalHealthProperties();
        properties.setAtRiskThreshold(-5.0);
        properties.setBehindThreshold(-25.0);
        goalHealthService = new GoalHealthServiceImpl(goalRepository, properties);

        testUser = new User("Alice", "alice@example.com", "hash");
        testUser.setId(1L);
        evaluationDate = LocalDate.of(2026, 10, 1);
    }

    private Goal createGoal(String title, Integer progress, LocalDate startDate, LocalDate targetDate, GoalStatus status) {
        Goal goal = new Goal(testUser, title, GoalCategory.CAREER);
        goal.setProgress(progress != null ? progress : 0);
        goal.setStartDate(startDate);
        goal.setTargetDate(targetDate);
        goal.setStatus(status != null ? status : GoalStatus.ACTIVE);
        return goal;
    }

    @Test
    @DisplayName("1. Completed by Progress: Progress >= 100% returns COMPLETED")
    void evaluate_CompletedByProgress_ReturnsCompleted() {
        Goal goal = createGoal("Learn Spring", 100, evaluationDate.minusDays(10), evaluationDate.plusDays(10), GoalStatus.ACTIVE);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.COMPLETED);
        assertThat(dto.getExpectedProgress()).isEqualTo(100.0);
        assertThat(dto.getDelta()).isEqualTo(0.0);
        assertThat(dto.getReason()).isEqualTo("Goal is completed.");
    }

    @Test
    @DisplayName("2. Completed by Status: Status == COMPLETED with < 100% progress returns COMPLETED")
    void evaluate_CompletedByStatus_ReturnsCompleted() {
        Goal goal = createGoal("Learn Spring", 50, evaluationDate.minusDays(10), evaluationDate.plusDays(10), GoalStatus.COMPLETED);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.COMPLETED);
        assertThat(dto.getExpectedProgress()).isEqualTo(100.0);
        assertThat(dto.getDelta()).isEqualTo(-50.0);
        assertThat(dto.getReason()).isEqualTo("Goal is completed.");
    }

    @Test
    @DisplayName("3. No Target Date: targetDate is null returns ON_TRACK with expected progress 0.0")
    void evaluate_NoTargetDate_ReturnsOnTrack() {
        Goal goal = createGoal("Open-ended reading", 30, evaluationDate.minusDays(10), null, GoalStatus.ACTIVE);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.ON_TRACK);
        assertThat(dto.getExpectedProgress()).isEqualTo(0.0);
        assertThat(dto.getDelta()).isEqualTo(30.0);
        assertThat(dto.getReason()).contains("No target date set");
    }

    @Test
    @DisplayName("4. Missing Start Date: Falls back to createdAt as effective start anchor")
    void evaluate_MissingStartDate_UsesCreatedAt() {
        Goal goal = createGoal("Project Launch", 50, null, evaluationDate.plusDays(10), GoalStatus.ACTIVE);
        goal.setCreatedAt(evaluationDate.minusDays(10).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());

        // totalDays = 20, elapsedDays = 10 -> expectedProgress = 50.0, actualProgress = 50.0 -> delta = 0.0 -> ON_TRACK
        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getEffectiveStartDate()).isEqualTo(evaluationDate.minusDays(10));
        assertThat(dto.getHealth()).isEqualTo(GoalHealth.ON_TRACK);
        assertThat(dto.getExpectedProgress()).isEqualTo(50.0);
        assertThat(dto.getDelta()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("5. Missing Both Start Date and CreatedAt: Data-integrity fallback handled safely")
    void evaluate_MissingStartDateAndCreatedAt_HandlesGracefully() {
        Goal goal = createGoal("Unanchored Goal", 20, null, evaluationDate.plusDays(10), GoalStatus.ACTIVE);
        goal.setCreatedAt(null);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getEffectiveStartDate()).isNull();
        assertThat(dto.getHealth()).isEqualTo(GoalHealth.ON_TRACK);
        assertThat(dto.getExpectedProgress()).isEqualTo(0.0);
        assertThat(dto.getDelta()).isEqualTo(20.0);
        assertThat(dto.getReason()).contains("Missing start date and creation date anchor");
    }

    @Test
    @DisplayName("6. Past Deadline: Target date in past returns BEHIND")
    void evaluate_PastDeadline_ReturnsBehind() {
        Goal goal = createGoal("Overdue Goal", 80, evaluationDate.minusDays(30), evaluationDate.minusDays(5), GoalStatus.ACTIVE);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.BEHIND);
        assertThat(dto.getExpectedProgress()).isEqualTo(100.0);
        assertThat(dto.getDelta()).isEqualTo(-20.0);
        assertThat(dto.getReason()).contains("Target date has passed");
    }

    @Test
    @DisplayName("7. Invalid Date Range: Target date precedes start date handled deterministically")
    void evaluate_InvalidDateRange_HandlesGracefully() {
        // Target date is before start date, but target date is in future relative to evaluationDate
        LocalDate futureTarget = evaluationDate.plusDays(5);
        LocalDate evenLaterStart = evaluationDate.plusDays(15);
        Goal goal = createGoal("Inverted Dates", 10, evenLaterStart, futureTarget, GoalStatus.ACTIVE);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.ON_TRACK);
        assertThat(dto.getExpectedProgress()).isEqualTo(0.0);
        assertThat(dto.getReason()).contains("precedes start date");
    }

    @Test
    @DisplayName("8. Zero-Duration Future: S == D > T returns ON_TRACK with expected 0.0")
    void evaluate_ZeroDuration_Future_ReturnsOnTrack() {
        LocalDate futureDate = evaluationDate.plusDays(5);
        Goal goal = createGoal("One-day Workshop", 0, futureDate, futureDate, GoalStatus.ACTIVE);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.ON_TRACK);
        assertThat(dto.getExpectedProgress()).isEqualTo(0.0);
        assertThat(dto.getDelta()).isEqualTo(0.0);
        assertThat(dto.getReason()).contains("Single-day goal scheduled for future date");
    }

    @Test
    @DisplayName("9. Zero-Duration Today (Exact -5.0 Boundary): S == D == T, actual 95% -> ON_TRACK")
    void evaluate_ZeroDuration_Today_ExactMinus5Boundary_ReturnsOnTrack() {
        Goal goal = createGoal("One-day Event", 95, evaluationDate, evaluationDate, GoalStatus.ACTIVE);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.ON_TRACK);
        assertThat(dto.getExpectedProgress()).isEqualTo(100.0);
        assertThat(dto.getDelta()).isEqualTo(-5.0);
        assertThat(dto.getReason()).contains("Single-day goal due today");
    }

    @Test
    @DisplayName("10. Zero-Duration Today (-5.1 Boundary): S == D == T, actual 94.9% -> AT_RISK")
    void evaluate_ZeroDuration_Today_Minus5Point1Boundary_ReturnsAtRisk() {
        Goal goal = createGoal("One-day Event", 94, evaluationDate, evaluationDate, GoalStatus.ACTIVE);
        // actual 94.0 -> delta = -6.0 <= -5.1 -> AT_RISK
        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.AT_RISK);
        assertThat(dto.getExpectedProgress()).isEqualTo(100.0);
        assertThat(dto.getDelta()).isEqualTo(-6.0);
    }

    @Test
    @DisplayName("11. Zero-Duration Today (Exact -25.0 Boundary): S == D == T, actual 75% -> AT_RISK")
    void evaluate_ZeroDuration_Today_ExactMinus25Boundary_ReturnsAtRisk() {
        Goal goal = createGoal("One-day Event", 75, evaluationDate, evaluationDate, GoalStatus.ACTIVE);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.AT_RISK);
        assertThat(dto.getExpectedProgress()).isEqualTo(100.0);
        assertThat(dto.getDelta()).isEqualTo(-25.0);
    }

    @Test
    @DisplayName("12. Zero-Duration Today (-25.1 Boundary): S == D == T, actual 74% -> BEHIND")
    void evaluate_ZeroDuration_Today_Minus25Point1Boundary_ReturnsBehind() {
        Goal goal = createGoal("One-day Event", 74, evaluationDate, evaluationDate, GoalStatus.ACTIVE);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.BEHIND);
        assertThat(dto.getExpectedProgress()).isEqualTo(100.0);
        assertThat(dto.getDelta()).isEqualTo(-26.0);
    }

    @Test
    @DisplayName("13. Zero-Duration Past: S == D < T returns BEHIND")
    void evaluate_ZeroDuration_Past_ReturnsBehind() {
        LocalDate pastDate = evaluationDate.minusDays(2);
        Goal goal = createGoal("Past One-day Event", 50, pastDate, pastDate, GoalStatus.ACTIVE);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.BEHIND);
        assertThat(dto.getExpectedProgress()).isEqualTo(100.0);
        assertThat(dto.getReason()).contains("Target date has passed");
    }

    @Test
    @DisplayName("14. Normal Schedule (Exact -5.0 Boundary): expected = 50.0, actual = 45.0 -> ON_TRACK")
    void evaluate_NormalSchedule_ExactMinus5Boundary_ReturnsOnTrack() {
        // S = -10, D = +10 -> total = 20, elapsed = 10 -> expected = 50.0
        Goal goal = createGoal("Normal Goal", 45, evaluationDate.minusDays(10), evaluationDate.plusDays(10), GoalStatus.ACTIVE);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.ON_TRACK);
        assertThat(dto.getExpectedProgress()).isEqualTo(50.0);
        assertThat(dto.getDelta()).isEqualTo(-5.0);
    }

    @Test
    @DisplayName("15. Normal Schedule (-5.1 Boundary): expected = 50.0, actual = 44.0 (delta = -6.0) -> AT_RISK")
    void evaluate_NormalSchedule_Minus5Point1Boundary_ReturnsAtRisk() {
        Goal goal = createGoal("Normal Goal", 44, evaluationDate.minusDays(10), evaluationDate.plusDays(10), GoalStatus.ACTIVE);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.AT_RISK);
        assertThat(dto.getExpectedProgress()).isEqualTo(50.0);
        assertThat(dto.getDelta()).isEqualTo(-6.0);
    }

    @Test
    @DisplayName("16. Normal Schedule (Exact -25.0 Boundary): expected = 50.0, actual = 25.0 -> AT_RISK")
    void evaluate_NormalSchedule_ExactMinus25Boundary_ReturnsAtRisk() {
        Goal goal = createGoal("Normal Goal", 25, evaluationDate.minusDays(10), evaluationDate.plusDays(10), GoalStatus.ACTIVE);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.AT_RISK);
        assertThat(dto.getExpectedProgress()).isEqualTo(50.0);
        assertThat(dto.getDelta()).isEqualTo(-25.0);
    }

    @Test
    @DisplayName("17. Normal Schedule (-25.1 Boundary): expected = 50.0, actual = 24.0 (delta = -26.0) -> BEHIND")
    void evaluate_NormalSchedule_Minus25Point1Boundary_ReturnsBehind() {
        Goal goal = createGoal("Normal Goal", 24, evaluationDate.minusDays(10), evaluationDate.plusDays(10), GoalStatus.ACTIVE);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.BEHIND);
        assertThat(dto.getExpectedProgress()).isEqualTo(50.0);
        assertThat(dto.getDelta()).isEqualTo(-26.0);
    }

    @Test
    @DisplayName("18. Future Start Date: T < S < D -> expected = 0.0, ON_TRACK")
    void evaluate_FutureStartDate_ReturnsOnTrack() {
        Goal goal = createGoal("Future Goal", 0, evaluationDate.plusDays(5), evaluationDate.plusDays(25), GoalStatus.ACTIVE);

        GoalHealthEvaluationDto dto = goalHealthService.evaluateWithDetails(goal, evaluationDate);

        assertThat(dto.getHealth()).isEqualTo(GoalHealth.ON_TRACK);
        assertThat(dto.getExpectedProgress()).isEqualTo(0.0);
        assertThat(dto.getDelta()).isEqualTo(0.0);
        assertThat(dto.getReason()).contains("Goal has not started yet");
    }

    @Test
    @DisplayName("Overview aggregation computes correct status counts")
    void getGoalHealthOverview_AggregatesCorrectly() {
        Goal g1 = createGoal("G1", 100, evaluationDate.minusDays(10), evaluationDate.plusDays(10), GoalStatus.ACTIVE); // COMPLETED
        Goal g2 = createGoal("G2", 50, evaluationDate.minusDays(10), evaluationDate.plusDays(10), GoalStatus.ACTIVE); // ON_TRACK
        Goal g3 = createGoal("G3", 30, evaluationDate.minusDays(10), evaluationDate.plusDays(10), GoalStatus.ACTIVE); // AT_RISK
        Goal g4 = createGoal("G4", 0, evaluationDate.minusDays(20), evaluationDate.minusDays(2), GoalStatus.ACTIVE); // BEHIND

        when(goalRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(g1, g2, g3, g4));

        GoalHealthOverviewResponse response = goalHealthService.getGoalHealthOverview(1L);

        assertThat(response.getTotalGoals()).isEqualTo(4);
        assertThat(response.getCompletedCount()).isEqualTo(1);
        assertThat(response.getOnTrackCount()).isEqualTo(1);
        assertThat(response.getAtRiskCount()).isEqualTo(1);
        assertThat(response.getBehindCount()).isEqualTo(1);
        assertThat(response.getEvaluations()).hasSize(4);
    }

    @Test
    @DisplayName("getGoalHealth throws ResourceNotFoundException for unknown or cross-user goal")
    void getGoalHealth_NotFound_ThrowsException() {
        when(goalRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalHealthService.getGoalHealth(1L, 999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Goal not found");
    }
}
