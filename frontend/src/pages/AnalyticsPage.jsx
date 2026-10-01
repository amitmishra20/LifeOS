import React, { useState, useEffect, useCallback } from 'react';
import analyticsService from '../services/analyticsService';
import ProductivityHeroCard from '../components/analytics/ProductivityHeroCard';
import AnalyticsMetricCard from '../components/analytics/AnalyticsMetricCard';
import {
  IconGoals,
  IconTasks,
  IconHabits,
  IconLearning,
} from '../components/ui/Icons';
import LoadingState from '../components/ui/LoadingState';
import Button from '../components/ui/Button';
import './AnalyticsPage.css';

export const AnalyticsPage = () => {
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchAnalytics = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      const res = await analyticsService.getDashboard();
      setData(res);
    } catch (err) {
      console.error('Failed to load analytics dashboard:', err);
      setError(err.response?.data?.message || err.message || 'Failed to load analytics');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchAnalytics();
  }, [fetchAnalytics]);

  if (isLoading) {
    return (
      <div className="analytics-page">
        <LoadingState message="Aggregating workspace analytics..." />
      </div>
    );
  }

  if (error) {
    return (
      <div className="analytics-page">
        <div className="analytics-page__error" role="alert">
          <h2>Unable to load analytics</h2>
          <p>{error}</p>
          <Button variant="secondary" onClick={fetchAnalytics}>
            Try Again
          </Button>
        </div>
      </div>
    );
  }

  const {
    productivity = { score: 0, weights: {}, components: {}, activeDomainCount: 0, totalDomainCount: 4 },
    goals = { progressPercentage: 0, totalGoals: 0, activeGoals: 0, completedGoals: 0, totalMilestones: 0, completedMilestones: 0 },
    tasks = { completionRate: 0, totalTasks: 0, completedTasks: 0, todoTasks: 0, inProgressTasks: 0, overdueTasks: 0 },
    habits = { consistencyRate: 0, activeHabitsCount: 0, totalActiveLogsInWindow: 0, observationWindowDays: 30 },
    learning = { activityScore: 0, activeItemsCount: 0, completedItemsCount: 0, totalSessionsCount: 0, totalMinutesLearned: 0 },
  } = data || {};

  const isZeroData = productivity.activeDomainCount === 0;

  return (
    <div className="analytics-page" data-testid="analytics-page">
      {/* Page Header */}
      <header className="analytics-page__header">
        <div className="analytics-page__header-left">
          <span className="analytics-page__kicker">REFLECT & SYNTHESIZE</span>
          <h1 className="analytics-page__title">Progress & Analytics</h1>
          <p className="analytics-page__subtitle">
            Systemic transparency across goals, tasks, habits, and learning mastery.
          </p>
        </div>
        <div className="analytics-page__actions">
          <Button variant="ghost" size="sm" onClick={fetchAnalytics} data-testid="refresh-analytics-btn">
            Refresh
          </Button>
        </div>
      </header>

      {/* Productivity Score Hero */}
      <ProductivityHeroCard productivity={productivity} />

      {/* Zero Data Callout */}
      {isZeroData && (
        <div className="analytics-page__empty-banner" data-testid="analytics-empty-banner">
          <h3 className="analytics-page__empty-banner-title">No Active Domain Data</h3>
          <p className="analytics-page__empty-banner-text">
            Start creating goals, tasks, habits, or learning items to see real-time synthesis and progress calculations.
          </p>
        </div>
      )}

      {/* Domain Cards Grid */}
      <div className="analytics-page__grid">
        {/* Goals Progress Card */}
        <AnalyticsMetricCard
          dataTestId="analytics-goals-card"
          icon={<IconGoals />}
          title="Goal Progress"
          mainValue={goals.progressPercentage}
          mainLabel="Completed Milestones / Total Milestones"
          progressPercentage={goals.progressPercentage}
          isActive={productivity.components?.goalProgress?.active}
          stats={[
            { label: 'Active Goals', value: goals.activeGoals },
            { label: 'Completed Goals', value: goals.completedGoals },
            { label: 'Milestones Completed', value: `${goals.completedMilestones} / ${goals.totalMilestones}` },
            { label: 'Total Goals', value: goals.totalGoals },
          ]}
        />

        {/* Task Completion Card */}
        <AnalyticsMetricCard
          dataTestId="analytics-tasks-card"
          icon={<IconTasks />}
          title="Task Completion"
          mainValue={tasks.completionRate}
          mainLabel="Completed / Eligible Tasks"
          progressPercentage={tasks.completionRate}
          isActive={productivity.components?.taskCompletion?.active}
          stats={[
            { label: 'Completed', value: tasks.completedTasks },
            { label: 'In Progress', value: tasks.inProgressTasks },
            { label: 'To Do', value: tasks.todoTasks },
            { label: 'Overdue', value: tasks.overdueTasks },
          ]}
        />

        {/* Habit Consistency Card */}
        <AnalyticsMetricCard
          dataTestId="analytics-habits-card"
          icon={<IconHabits />}
          title="Habit Consistency"
          mainValue={habits.consistencyRate}
          mainLabel="30-Day Active Habit Average"
          progressPercentage={habits.consistencyRate}
          isActive={productivity.components?.habitConsistency?.active}
          stats={[
            { label: 'Active Habits', value: habits.activeHabitsCount },
            { label: 'Logs in 30d Window', value: habits.totalActiveLogsInWindow },
            { label: 'Observation Window', value: `${habits.observationWindowDays} days` },
            { label: 'Frequency Modes', value: 'Daily / Target' },
          ]}
        />

        {/* Learning Activity Card */}
        <AnalyticsMetricCard
          dataTestId="analytics-learning-card"
          icon={<IconLearning />}
          title="Learning Activity"
          mainValue={learning.activityScore}
          mainLabel="Mastery Progress & Session Practice"
          progressPercentage={learning.activityScore}
          isActive={productivity.components?.learningActivity?.active}
          stats={[
            { label: 'Active Items', value: learning.activeItemsCount },
            { label: 'Completed Items', value: learning.completedItemsCount },
            { label: 'Sessions Logged', value: learning.totalSessionsCount },
            { label: 'Minutes Studied', value: `${learning.totalMinutesLearned}m` },
          ]}
        />
      </div>
    </div>
  );
};

export default AnalyticsPage;
