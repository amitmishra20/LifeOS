package com.lifeos.learning.service;

import com.lifeos.common.exception.ResourceNotFoundException;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.learning.dto.CreateLearningItemRequest;
import com.lifeos.learning.dto.CreateLearningSessionRequest;
import com.lifeos.learning.dto.LearningItemResponse;
import com.lifeos.learning.dto.LearningSessionResponse;
import com.lifeos.learning.dto.UpdateLearningItemRequest;
import com.lifeos.learning.dto.UpdateLearningProgressRequest;
import com.lifeos.learning.dto.UpdateLearningStatusRequest;
import com.lifeos.learning.entity.LearningCategory;
import com.lifeos.learning.entity.LearningItem;
import com.lifeos.learning.entity.LearningStatus;
import com.lifeos.learning.repository.LearningItemRepository;
import com.lifeos.learning.repository.LearningSessionRepository;
import com.lifeos.user.entity.User;
import com.lifeos.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LearningServiceTest {

    @Mock
    private LearningItemRepository learningItemRepository;

    @Mock
    private LearningSessionRepository learningSessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private LearningServiceImpl learningService;

    private User user;
    private LearningItem item;

    @BeforeEach
    void setUp() {
        user = new User("Alice", "alice@example.com", "hash");
        user.setId(1L);

        item = new LearningItem(user, "Algorithms", LearningCategory.TECHNICAL, 100);
        item.setId(10L);
    }

    @Test
    @DisplayName("createLearningItem: should succeed with defaults when optional fields are null")
    void createLearningItem_Defaults() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(learningItemRepository.save(any(LearningItem.class))).thenAnswer(inv -> {
            LearningItem i = inv.getArgument(0);
            i.setId(10L);
            return i;
        });

        CreateLearningItemRequest req = new CreateLearningItemRequest();
        req.setTitle("Calculus");

        LearningItemResponse res = learningService.createLearningItem(1L, req);

        assertThat(res.getTitle()).isEqualTo("Calculus");
        assertThat(res.getCategory()).isEqualTo(LearningCategory.TECHNICAL);
        assertThat(res.getTargetProgress()).isEqualTo(100);
        assertThat(res.getCurrentProgress()).isEqualTo(0);
        assertThat(res.getStatus()).isEqualTo(LearningStatus.ACTIVE);
    }

    @Test
    @DisplayName("createLearningItem: should reject invalid progress values")
    void createLearningItem_InvalidProgress() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        CreateLearningItemRequest req = new CreateLearningItemRequest();
        req.setTitle("Calculus");
        req.setTargetProgress(0);

        assertThatThrownBy(() -> learningService.createLearningItem(1L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Target progress must be between 1 and 100");

        req.setTargetProgress(100);
        req.setCurrentProgress(-5);
        assertThatThrownBy(() -> learningService.createLearningItem(1L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Current progress must be between 0 and 100");
    }

    @Test
    @DisplayName("updateLearningStatus: should reject invalid transitions with explainable error")
    void updateLearningStatus_InvalidTransitions() {
        item.setStatus(LearningStatus.COMPLETED);
        when(learningItemRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(item));

        UpdateLearningStatusRequest req = new UpdateLearningStatusRequest(LearningStatus.PAUSED);

        assertThatThrownBy(() -> learningService.updateLearningStatus(1L, 10L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid status transition from COMPLETED to PAUSED");
    }

    @Test
    @DisplayName("createLearningSession: reject future dates and zero duration")
    void createLearningSession_Validations() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(learningItemRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(item));

        CreateLearningSessionRequest futureReq = new CreateLearningSessionRequest(
                LocalDate.now().plusDays(2), 30, "Topic", null
        );

        assertThatThrownBy(() -> learningService.createLearningSession(1L, 10L, futureReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Session date cannot be in the future");

        CreateLearningSessionRequest zeroReq = new CreateLearningSessionRequest(
                LocalDate.now(), 0, "Topic", null
        );

        assertThatThrownBy(() -> learningService.createLearningSession(1L, 10L, zeroReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Duration must be greater than 0");
    }

    @Test
    @DisplayName("createLearningSession: reject session when learning item is ARCHIVED")
    void createLearningSession_ArchivedItemRejected() {
        item.setStatus(LearningStatus.ARCHIVED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(learningItemRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(item));

        CreateLearningSessionRequest req = new CreateLearningSessionRequest(
                LocalDate.now(), 30, "Topic", null
        );

        assertThatThrownBy(() -> learningService.createLearningSession(1L, 10L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot log session for an archived learning item");
    }

    @Test
    @DisplayName("deleteLearningSession: throw 404 when session not found or owned by different user")
    void deleteLearningSession_NotFound() {
        when(learningItemRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(item));
        when(learningSessionRepository.findByIdAndLearningItemIdAndUserId(99L, 10L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> learningService.deleteLearningSession(1L, 10L, 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Learning session not found");
    }

    @Test
    @DisplayName("updateLearningItem: clears goal when goalId is null")
    void updateLearningItem_ClearsGoal() {
        Goal goal = new Goal(user, "Goal", GoalCategory.HEALTH);
        goal.setId(5L);
        item.setGoal(goal);

        when(learningItemRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(item));
        when(learningItemRepository.save(any(LearningItem.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateLearningItemRequest req = new UpdateLearningItemRequest("New Title", LearningCategory.LANGUAGE, 90);
        req.setGoalId(null);

        LearningItemResponse res = learningService.updateLearningItem(1L, 10L, req);

        assertThat(item.getGoal()).isNull();
        assertThat(res.getGoalId()).isNull();
    }
}
