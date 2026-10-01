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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final TaskRepository taskRepository;
    private final HabitRepository habitRepository;
    private final LearningItemRepository learningItemRepository;
    private final NoteRepository noteRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            GoalRepository goalRepository,
            TaskRepository taskRepository,
            HabitRepository habitRepository,
            LearningItemRepository learningItemRepository,
            NoteRepository noteRepository
    ) {
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
        this.taskRepository = taskRepository;
        this.habitRepository = habitRepository;
        this.learningItemRepository = learningItemRepository;
        this.noteRepository = noteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileSummaryResponse getProfileSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        long totalGoals = goalRepository.countByUserId(userId);
        long totalTasks = taskRepository.countByUserId(userId);
        long totalHabits = habitRepository.countByUserId(userId);
        long totalLearningItems = learningItemRepository.countByUserId(userId);
        long totalNotes = noteRepository.countByUserId(userId);

        return new UserProfileSummaryResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                totalGoals,
                totalTasks,
                totalHabits,
                totalLearningItems,
                totalNotes
        );
    }

    @Override
    @Transactional
    public void deleteCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Database foreign key constraints (ON DELETE CASCADE) cascade deletion across all
        // child tables: goals, milestones, tasks, habits, habit_logs, habit_pause_intervals,
        // learning_items, learning_sessions, calendar_events, notes.
        userRepository.delete(user);
    }
}
