import { useState, useMemo, useCallback, useEffect } from 'react';
import { buildDashboardViewModel } from './buildDashboardViewModel';
import goalService from '../services/goalService';
import recommendationService from '../services/recommendationService';
import taskService from '../services/taskService';
import habitService from '../services/habitService';

const getInitialDataset = () => ({ user: null, goals: [], milestones: [], tasks: [], habits: [], guidance: null });

/**
 * useDashboardViewModel
 * 
 * Manages the single authoritative domain dataset,
 * interactive state mutations (tasks, habits, waypoints, goals),
 * and computes the pure derived ViewModel.
 */
export const useDashboardViewModel = (currentUser = null) => {
  const [dataset, setDataset] = useState(() => getInitialDataset());
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const loadRealData = useCallback(async () => {
    if (!currentUser) return;
    try {
      const [goalsList, dailyData, todayHabits] = await Promise.all([
        goalService.getGoals().catch(() => []),
        recommendationService.getDailyFocus().catch(() => ({ items: [] })),
        habitService.getTodayHabits().catch(() => []),
      ]);

      let fullGoals = goalsList || [];
      if (fullGoals.length > 0) {
        try {
          const firstGoalDetail = await goalService.getGoalById(fullGoals[0].id);
          fullGoals = [firstGoalDetail, ...fullGoals.slice(1)];
        } catch {
          // fallback
        }
      }

      const mappedTasks = (dailyData?.items || []).map((item, idx) => ({
        id: item.task.id,
        title: item.task.title,
        completed: item.task.status === 'COMPLETED',
        priority: item.task.priority,
        duration: item.task.estimatedMinutes ? `${item.task.estimatedMinutes}m` : null,
        time: item.task.dueDate ? `Due ${item.task.dueDate}` : 'Today',
        category: item.task.goalTitle || 'Focus',
        goalId: item.task.goalId,
        milestoneId: item.task.milestoneId,
        whyReason: item.primaryReason,
        actionLabel: 'Focus Now',
        isPrimary: idx === 0,
      }));

      setDataset((prev) => ({
        ...prev,
        goals: fullGoals,
        tasks: mappedTasks,
        habits: todayHabits || [],
      }));
    } catch (err) {
      console.error('Failed to load dashboard dataset', err);
    }
  }, [currentUser]);

  // Load real goals, focus & habits into dashboard dataset
  useEffect(() => {
    loadRealData();
  }, [loadRealData]);

  // Signature Interaction States
  const [expandedWaypointId, setExpandedWaypointId] = useState(null);
  const [expandedGoalId, setExpandedGoalId] = useState(null);
  const [lastCompletedTaskId, setLastCompletedTaskId] = useState(null);

  // Signature Interaction: Task Completion & Cascade
  const toggleTask = useCallback(async (taskId) => {
    try {
      setLastCompletedTaskId(taskId);
      setTimeout(() => setLastCompletedTaskId(null), 1200);

      await taskService.completeTask(taskId);
      await loadRealData();
    } catch (err) {
      console.error('Failed to toggle task from dashboard', err);
    }
  }, [loadRealData]);

  // Signature Interaction: Habit Cadence Rhythm Toggle
  const toggleHabit = useCallback(async (habitId, dateOrIdx) => {
    try {
      let date = null;
      if (typeof dateOrIdx === 'string' && dateOrIdx.includes('-')) {
        date = dateOrIdx;
      } else if (typeof dateOrIdx === 'number') {
        const targetHabit = dataset.habits.find((h) => h.id === habitId);
        if (targetHabit && targetHabit.history && targetHabit.history[dateOrIdx]) {
          const item = targetHabit.history[dateOrIdx];
          date = typeof item === 'object' ? item.date : null;
        }
      }

      const res = await habitService.toggleHabit(habitId, date);
      setDataset((prev) => ({
        ...prev,
        habits: prev.habits.map((h) => (h.id === habitId ? (res.habit || { ...h, completedToday: res.completed }) : h)),
      }));
    } catch (err) {
      console.error('Failed to toggle habit from dashboard', err);
    }
  }, [dataset.habits]);

  // Signature Interaction: Journey Waypoint Expansion
  const toggleWaypoint = useCallback((waypointId) => {
    setExpandedWaypointId((prev) => (prev === waypointId ? null : waypointId));
  }, []);

  // Signature Interaction: Goal Expansion
  const toggleGoal = useCallback((goalId) => {
    setExpandedGoalId((prev) => (prev === goalId ? null : goalId));
  }, []);

  // Reset or retry
  const retry = useCallback(() => {
    try {
      setIsLoading(true);
      setError(null);
      setDataset(getInitialDataset());
      setIsLoading(false);
    } catch (err) {
      setError(err.message || 'Failed to reload dataset');
      setIsLoading(false);
    }
  }, []);

  // Build the complete, derived DashboardViewModel
  const viewModel = useMemo(() => {
    return buildDashboardViewModel({
      ...dataset,
      user: currentUser || dataset.user,
    });
  }, [dataset, currentUser]);

  return {
    viewModel,
    toggleTask,
    toggleHabit,
    expandedWaypointId,
    toggleWaypoint,
    expandedGoalId,
    toggleGoal,
    lastCompletedTaskId,
    isLoading,
    error,
    retry,
  };
};

export default useDashboardViewModel;
