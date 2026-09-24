package com.lifeos.goal.repository;

import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.entity.GoalPriority;
import com.lifeos.goal.entity.GoalStatus;
import com.lifeos.user.entity.User;
import com.lifeos.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class GoalRepositoryTest {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private User userA;
    private User userB;

    @BeforeEach
    void setUp() {
        userA = userRepository.saveAndFlush(new User("User Alpha", "alpha@example.com", "hashA"));
        userB = userRepository.saveAndFlush(new User("User Beta", "beta@example.com", "hashB"));
    }

    @Test
    @DisplayName("Should save goal linked to user and verify default values and auditing")
    void shouldSaveGoalLinkedToUser() {
        Goal goal = new Goal(userA, "Master Spring Boot 3", GoalCategory.CAREER);
        goal.setDescription("In-depth learning of enterprise microservices and modular monoliths");
        goal.setPriority(GoalPriority.HIGH);
        goal.setStartDate(LocalDate.of(2026, 1, 1));
        goal.setTargetDate(LocalDate.of(2026, 6, 30));

        Goal savedGoal = goalRepository.saveAndFlush(goal);

        assertThat(savedGoal.getId()).isNotNull();
        assertThat(savedGoal.getUser().getId()).isEqualTo(userA.getId());
        assertThat(savedGoal.getStatus()).isEqualTo(GoalStatus.ACTIVE);
        assertThat(savedGoal.getProgress()).isEqualTo(0);
        assertThat(savedGoal.getPriority()).isEqualTo(GoalPriority.HIGH);
        assertThat(savedGoal.getCategory()).isEqualTo(GoalCategory.CAREER);
        assertThat(savedGoal.getCreatedAt()).isNotNull();
        assertThat(savedGoal.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find goals by userId ordered by createdAt desc")
    void shouldFindByUserIdOrderByCreatedAtDesc() {
        Goal goal1 = new Goal(userA, "First Goal", GoalCategory.HEALTH);
        goalRepository.saveAndFlush(goal1);

        Goal goal2 = new Goal(userA, "Second Goal", GoalCategory.PERSONAL);
        goalRepository.saveAndFlush(goal2);

        List<Goal> goals = goalRepository.findByUserIdOrderByCreatedAtDesc(userA.getId());
        assertThat(goals).hasSize(2);
        assertThat(goals.get(0).getTitle()).isEqualTo("Second Goal");
        assertThat(goals.get(1).getTitle()).isEqualTo("First Goal");
    }

    @Test
    @DisplayName("Should find goals by userId and status")
    void shouldFindByUserIdAndStatus() {
        Goal activeGoal = new Goal(userA, "Active Goal", GoalCategory.CAREER);
        activeGoal.setStatus(GoalStatus.ACTIVE);
        goalRepository.saveAndFlush(activeGoal);

        Goal completedGoal = new Goal(userA, "Completed Goal", GoalCategory.FINANCE);
        completedGoal.setStatus(GoalStatus.COMPLETED);
        goalRepository.saveAndFlush(completedGoal);

        List<Goal> activeGoals = goalRepository.findByUserIdAndStatus(userA.getId(), GoalStatus.ACTIVE);
        assertThat(activeGoals).hasSize(1);
        assertThat(activeGoals.get(0).getTitle()).isEqualTo("Active Goal");

        List<Goal> completedGoals = goalRepository.findByUserIdAndStatus(userA.getId(), GoalStatus.COMPLETED);
        assertThat(completedGoals).hasSize(1);
        assertThat(completedGoals.get(0).getTitle()).isEqualTo("Completed Goal");
    }

    @Test
    @DisplayName("Should enforce user isolation: User B cannot access User A's goal")
    void shouldEnforceUserIsolation() {
        Goal goalA = new Goal(userA, "Confidential Goal A", GoalCategory.CAREER);
        Goal savedGoalA = goalRepository.saveAndFlush(goalA);

        // Owner can access
        Optional<Goal> foundForOwner = goalRepository.findByIdAndUserId(savedGoalA.getId(), userA.getId());
        assertThat(foundForOwner).isPresent();
        assertThat(goalRepository.existsByIdAndUserId(savedGoalA.getId(), userA.getId())).isTrue();

        // Non-owner cannot access
        Optional<Goal> foundForOther = goalRepository.findByIdAndUserId(savedGoalA.getId(), userB.getId());
        assertThat(foundForOther).isEmpty();
        assertThat(goalRepository.existsByIdAndUserId(savedGoalA.getId(), userB.getId())).isFalse();
    }

    @Test
    @DisplayName("Should cascade delete goals when user is deleted")
    void shouldCascadeDeleteGoalsWhenUserIsDeleted() {
        Goal goal = new Goal(userA, "Ephemeral Goal", GoalCategory.EDUCATION);
        Goal savedGoal = goalRepository.saveAndFlush(goal);
        Long goalId = savedGoal.getId();

        entityManager.flush();
        entityManager.clear();

        // Delete user directly via native or repo
        User managedUser = userRepository.findById(userA.getId()).orElseThrow();
        userRepository.delete(managedUser);
        userRepository.flush();
        entityManager.clear();

        assertThat(userRepository.findById(userA.getId())).isEmpty();
        assertThat(goalRepository.findById(goalId)).isEmpty();
    }
}
