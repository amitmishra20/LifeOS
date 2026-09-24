import { useState, useMemo, useCallback } from 'react';
import { buildDashboardViewModel } from './buildDashboardViewModel';

const getInitialDataset = () => ({ user: null, goals: [], milestones: [], tasks: [], habits: [], guidance: null });

/**
 * useDashboardViewModel
 * 
 * Manages the single authoritative domain dataset,
 * interactive state mutations (tasks, habits, waypoints, goals),
 * and computes the pure derived ViewModel.
 */
export const useDashboardViewModel = () => {
  const [dataset, setDataset] = useState(() => getInitialDataset());
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  // Signature Interaction States
  const [expandedWaypointId, setExpandedWaypointId] = useState(null);
  const [expandedGoalId, setExpandedGoalId] = useState(null);
  const [lastCompletedTaskId, setLastCompletedTaskId] = useState(null);

  // Signature Interaction: Task Completion & Cascade
  const toggleTask = useCallback((taskId) => {
    setDataset((prev) => {
      const targetTask = prev.tasks.find((t) => t.id === taskId);
      const isCompleting = targetTask ? !targetTask.completed : false;

      const updatedTasks = prev.tasks.map((task) =>
        task.id === taskId ? { ...task, completed: !task.completed } : task
      );

      if (isCompleting) {
        setLastCompletedTaskId(taskId);
        setTimeout(() => setLastCompletedTaskId(null), 1200);
      }

      return {
        ...prev,
        tasks: updatedTasks,
      };
    });
  }, []);

  // Signature Interaction: Habit Cadence Rhythm Toggle
  const toggleHabit = useCallback((habitId, dayIndex) => {
    setDataset((prev) => {
      const updatedHabits = prev.habits.map((habit) => {
        if (habit.id !== habitId) return habit;
        const newHistory = [...habit.history];
        newHistory[dayIndex] = !newHistory[dayIndex];
        const completedCount = newHistory.filter(Boolean).length;
        const rate = newHistory.length > 0 ? Math.round((completedCount / newHistory.length) * 100) : 0;
        return {
          ...habit,
          history: newHistory,
          completedDaysCount: completedCount,
          consistencyRate: rate,
        };
      });

      return {
        ...prev,
        habits: updatedHabits,
      };
    });
  }, []);

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
    return buildDashboardViewModel(dataset);
  }, [dataset]);

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
