package com.lifeos.user.service;

import com.lifeos.common.exception.ResourceNotFoundException;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.habit.repository.HabitRepository;
import com.lifeos.learning.repository.LearningItemRepository;
import com.lifeos.notes.repository.NoteRepository;
import com.lifeos.task.repository.TaskRepository;
import com.lifeos.user.dto.UserProfileSummaryResponse;
import com.lifeos.user.entity.User;
import com.lifeos.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private LearningItemRepository learningItemRepository;

    @Mock
    private NoteRepository noteRepository;

    private UserServiceImpl userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userRepository,
                goalRepository,
                taskRepository,
                habitRepository,
                learningItemRepository,
                noteRepository
        );
        testUser = new User("Alice", "alice@example.com", "hash");
        testUser.setId(1L);
        testUser.setCreatedAt(Instant.now());
    }

    @Test
    @DisplayName("Should return user profile summary with total entity counts")
    void getProfileSummary_ReturnsAggregatedCounts() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(goalRepository.countByUserId(1L)).thenReturn(3L);
        when(taskRepository.countByUserId(1L)).thenReturn(12L);
        when(habitRepository.countByUserId(1L)).thenReturn(4L);
        when(learningItemRepository.countByUserId(1L)).thenReturn(2L);
        when(noteRepository.countByUserId(1L)).thenReturn(7L);

        UserProfileSummaryResponse response = userService.getProfileSummary(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Alice");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
        assertThat(response.getTotalGoals()).isEqualTo(3L);
        assertThat(response.getTotalTasks()).isEqualTo(12L);
        assertThat(response.getTotalHabits()).isEqualTo(4L);
        assertThat(response.getTotalLearningItems()).isEqualTo(2L);
        assertThat(response.getTotalNotes()).isEqualTo(7L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user does not exist")
    void getProfileSummary_UserNotFound_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfileSummary(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should delete user entity when deleteCurrentUser is called")
    void deleteCurrentUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteCurrentUser(1L);

        verify(userRepository).delete(testUser);
    }
}
