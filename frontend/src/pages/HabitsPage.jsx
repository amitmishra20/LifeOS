import React, { useState, useEffect, useCallback, useMemo } from 'react';
import habitService from '../services/habitService';
import HabitModal from '../components/habits/HabitModal';
import HabitRhythmRow from '../components/composites/HabitRhythmRow';
import Button from '../components/ui/Button';
import EmptyState from '../components/ui/EmptyState';
import LoadingState from '../components/ui/LoadingState';
import './HabitsPage.css';

const FILTER_TABS = [
  { key: 'ACTIVE', label: 'Active Rhythms' },
  { key: 'PAUSED', label: 'Paused' },
  { key: 'ARCHIVED', label: 'Archived' },
];

export const HabitsPage = () => {
  const [habits, setHabits] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('ACTIVE');

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingHabit, setEditingHabit] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [toastMessage, setToastMessage] = useState(null);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 2500);
  };

  const fetchHabits = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await habitService.getHabits();
      setHabits(data || []);
    } catch (err) {
      setError(err.message || 'Failed to load habits.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchHabits();
  }, [fetchHabits]);

  // Derived Summary Metrics
  const summary = useMemo(() => {
    const activeHabits = habits.filter((h) => h.status === 'ACTIVE');
    const activeCount = activeHabits.length;
    const completedToday = activeHabits.filter((h) => h.completedToday).length;
    const avgConsistency = activeCount > 0
      ? Math.round(activeHabits.reduce((acc, h) => acc + (h.consistencyRate || 0), 0) / activeCount)
      : 0;

    return {
      activeCount,
      completedToday,
      overallConsistency: avgConsistency,
    };
  }, [habits]);

  // Filtered Habits based on Tab
  const filteredHabits = useMemo(() => {
    return habits.filter((h) => h.status === activeTab);
  }, [habits, activeTab]);

  const handleCreateOrUpdateHabit = async (payload) => {
    setIsSubmitting(true);
    try {
      if (editingHabit) {
        const updated = await habitService.updateHabit(editingHabit.id, payload);
        setHabits((prev) => prev.map((h) => (h.id === updated.id ? updated : h)));
        showToast('Habit updated successfully.');
      } else {
        const created = await habitService.createHabit(payload);
        setHabits((prev) => [created, ...prev]);
        showToast('Habit created successfully.');
      }
      setIsModalOpen(false);
      setEditingHabit(null);
    } catch (err) {
      alert(err.response?.data?.message || err.message || 'Failed to save habit.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleToggleHabit = async (habitId, dateOrIdx) => {
    try {
      const date = typeof dateOrIdx === 'string' ? dateOrIdx : null;
      const res = await habitService.toggleHabit(habitId, date);
      setHabits((prev) =>
        prev.map((h) => (h.id === habitId ? (res.habit ? res.habit : { ...h, completedToday: res.completed }) : h))
      );
      showToast(res.completed ? 'Practice completed.' : 'Practice unmarked.');
    } catch (err) {
      showToast(err.response?.data?.message || err.message || 'Failed to toggle practice.');
    }
  };

  const handleStatusChange = async (habit, newStatus) => {
    try {
      const updated = await habitService.updateHabitStatus(habit.id, newStatus);
      setHabits((prev) => prev.map((h) => (h.id === updated.id ? updated : h)));
      const msg = newStatus === 'PAUSED'
        ? 'Habit paused.'
        : newStatus === 'ARCHIVED'
        ? 'Habit archived.'
        : 'Habit reactivated.';
      showToast(msg);
    } catch (err) {
      alert(err.response?.data?.message || err.message || 'Failed to change habit status.');
    }
  };

  const handleDeleteHabit = async (habit) => {
    if (!window.confirm(`Permanently delete habit "${habit.title}"? This cannot be undone.`)) return;
    try {
      await habitService.deleteHabit(habit.id);
      setHabits((prev) => prev.filter((h) => h.id !== habit.id));
      showToast('Habit deleted.');
    } catch (err) {
      alert(err.response?.data?.message || err.message || 'Failed to delete habit.');
    }
  };

  return (
    <div className="lifeos-habits-page">
      {toastMessage && (
        <div className="lifeos-feedback-toast" role="status">
          {toastMessage}
        </div>
      )}

      {/* Header Section */}
      <header className="lifeos-habits-header">
        <div className="lifeos-habits-header__titles">
          <span className="lifeos-kicker">Daily Rhythms</span>
          <h1 className="lifeos-habits-header__title">Habits</h1>
          <p className="lifeos-habits-header__subtitle">
            Small, continuous practices that build the life you want.
          </p>
        </div>
        <div className="lifeos-habits-header__actions">
          <Button
            variant="primary"
            onClick={() => {
              setEditingHabit(null);
              setIsModalOpen(true);
            }}
            id="new-habit-btn"
          >
            + New Habit
          </Button>
        </div>
      </header>

      {/* Summary KPI Strip */}
      <section className="lifeos-habits-summary-strip" aria-label="Habit Summary">
        <div className="lifeos-habits-summary-card">
          <span className="lifeos-habits-summary-card__label">Active Habits</span>
          <strong className="lifeos-habits-summary-card__value">{summary.activeCount}</strong>
        </div>
        <div className="lifeos-habits-summary-card">
          <span className="lifeos-habits-summary-card__label">Today&apos;s Completed</span>
          <strong className="lifeos-habits-summary-card__value">
            {summary.completedToday} <small>/ {summary.activeCount}</small>
          </strong>
        </div>
        <div className="lifeos-habits-summary-card">
          <span className="lifeos-habits-summary-card__label">Overall Consistency</span>
          <strong className="lifeos-habits-summary-card__value">
            {summary.overallConsistency}%
          </strong>
        </div>
      </section>

      {/* Filter Tabs */}
      <nav className="lifeos-habits-nav" aria-label="Habit status filters">
        <div className="lifeos-habits-tabs">
          {FILTER_TABS.map((tab) => (
            <button
              key={tab.key}
              type="button"
              id={`habit-tab-${tab.key.toLowerCase()}`}
              className={`lifeos-habits-tab ${activeTab === tab.key ? 'lifeos-habits-tab--active' : ''}`}
              onClick={() => setActiveTab(tab.key)}
            >
              {tab.label}
              <span className="lifeos-habits-tab__badge">
                {habits.filter((h) => h.status === tab.key).length}
              </span>
            </button>
          ))}
        </div>
      </nav>

      {/* Main Content Area */}
      {isLoading ? (
        <LoadingState message="Loading habits..." />
      ) : error ? (
        <div className="lifeos-habits-error" role="alert">
          <p>{error}</p>
          <Button variant="secondary" onClick={fetchHabits}>
            Retry
          </Button>
        </div>
      ) : filteredHabits.length === 0 ? (
        <EmptyState
          title={
            activeTab === 'ACTIVE'
              ? 'No active rhythms yet'
              : activeTab === 'PAUSED'
              ? 'No paused rhythms'
              : 'No archived rhythms'
          }
          description={
            activeTab === 'ACTIVE'
              ? 'Create a habit to cultivate daily momentum and continuous practice.'
              : activeTab === 'PAUSED'
              ? 'Paused rhythms give you space to rest without breaking historical streaks.'
              : 'Archived habits preserve your completion history without cluttering your daily view.'
          }
          actionLabel={activeTab === 'ACTIVE' ? '+ Create Your First Habit' : undefined}
          onAction={
            activeTab === 'ACTIVE'
              ? () => {
                  setEditingHabit(null);
                  setIsModalOpen(true);
                }
              : undefined
          }
        />
      ) : (
        <div className="lifeos-habits-list" role="list">
          {filteredHabits.map((habit) => (
            <article key={habit.id} className="lifeos-habit-card" role="listitem">
              <div className="lifeos-habit-card__main">
                <HabitRhythmRow habit={habit} onToggleHabit={handleToggleHabit} />
              </div>

              {habit.description && (
                <p className="lifeos-habit-card__desc">{habit.description}</p>
              )}

              <div className="lifeos-habit-card__footer">
                <div className="lifeos-habit-card__meta-tags">
                  <span className="lifeos-habit-card__cadence-tag">
                    {habit.frequencyType === 'DAILY' && 'Every day'}
                    {habit.frequencyType === 'SPECIFIC_DAYS' &&
                      `${habit.targetPerWeek} selected days/wk`}
                    {habit.frequencyType === 'WEEKLY_TARGET' &&
                      `Flexible ${habit.targetPerWeek}x/wk`}
                  </span>
                  {habit.goalTitle && (
                    <span className="lifeos-habit-card__goal-tag">
                      🎯 {habit.goalTitle}
                    </span>
                  )}
                  {habit.status === 'PAUSED' && (
                    <span className="lifeos-habit-card__status-tag lifeos-habit-card__status-tag--paused">
                      Paused
                    </span>
                  )}
                  {habit.status === 'ARCHIVED' && (
                    <span className="lifeos-habit-card__status-tag lifeos-habit-card__status-tag--archived">
                      Archived
                    </span>
                  )}
                </div>

                <div className="lifeos-habit-card__actions">
                  <button
                    type="button"
                    className="lifeos-habit-card__action-btn"
                    onClick={() => {
                      setEditingHabit(habit);
                      setIsModalOpen(true);
                    }}
                  >
                    Edit
                  </button>

                  {habit.status === 'ACTIVE' && (
                    <button
                      type="button"
                      className="lifeos-habit-card__action-btn"
                      onClick={() => handleStatusChange(habit, 'PAUSED')}
                    >
                      Pause
                    </button>
                  )}

                  {habit.status === 'PAUSED' && (
                    <button
                      type="button"
                      className="lifeos-habit-card__action-btn lifeos-habit-card__action-btn--resume"
                      onClick={() => handleStatusChange(habit, 'ACTIVE')}
                    >
                      Resume
                    </button>
                  )}

                  {habit.status !== 'ARCHIVED' && (
                    <button
                      type="button"
                      className="lifeos-habit-card__action-btn"
                      onClick={() => handleStatusChange(habit, 'ARCHIVED')}
                    >
                      Archive
                    </button>
                  )}

                  {habit.status === 'ARCHIVED' && (
                    <button
                      type="button"
                      className="lifeos-habit-card__action-btn lifeos-habit-card__action-btn--reactivate"
                      onClick={() => handleStatusChange(habit, 'ACTIVE')}
                    >
                      Reactivate
                    </button>
                  )}

                  <button
                    type="button"
                    className="lifeos-habit-card__action-btn lifeos-habit-card__action-btn--delete"
                    onClick={() => handleDeleteHabit(habit)}
                  >
                    Delete
                  </button>
                </div>
              </div>
            </article>
          ))}
        </div>
      )}

      {/* Create / Edit Modal */}
      <HabitModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setEditingHabit(null);
        }}
        onSubmit={handleCreateOrUpdateHabit}
        habit={editingHabit}
        isSubmitting={isSubmitting}
      />
    </div>
  );
};

export default HabitsPage;
