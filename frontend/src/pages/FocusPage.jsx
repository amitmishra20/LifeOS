import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import recommendationService from '../services/recommendationService';
import taskService from '../services/taskService';
import PrimaryFocusCard from '../components/composites/PrimaryFocusCard';
import Button from '../components/ui/Button';
import EmptyState from '../components/ui/EmptyState';
import LoadingState from '../components/ui/LoadingState';
import TaskModal from '../components/tasks/TaskModal';
import './FocusPage.css';

export const FocusPage = () => {
  const navigate = useNavigate();

  const [dailyFocus, setDailyFocus] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [toastMessage, setToastMessage] = useState(null);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 2500);
  };

  const fetchDailyFocus = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await recommendationService.getDailyFocus();
      setDailyFocus(data);
    } catch (err) {
      setError(err.message || "Failed to load today's focus.");
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchDailyFocus();
  }, [fetchDailyFocus]);

  const handleToggleComplete = async (taskId) => {
    try {
      const updated = await taskService.completeTask(taskId);
      showToast(updated.status === 'COMPLETED' ? 'Focus completed.' : 'Focus reopened.');
      // Refresh recommendations to reflect updated priorities
      await fetchDailyFocus();
    } catch (err) {
      alert(err.message || 'Failed to toggle task completion.');
    }
  };

  const handleCreateTask = async (payload) => {
    setIsSubmitting(true);
    try {
      await taskService.createTask(payload);
      showToast('Task created.');
      setIsTaskModalOpen(false);
      await fetchDailyFocus();
    } catch (err) {
      alert(err.message || 'Failed to create task.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const dateHeading = dailyFocus?.date
    ? new Date(dailyFocus.date + 'T00:00:00').toLocaleDateString('en-US', {
        weekday: 'long',
        month: 'short',
        day: 'numeric',
      })
    : new Date().toLocaleDateString('en-US', {
        weekday: 'long',
        month: 'short',
        day: 'numeric',
      });

  if (isLoading && !dailyFocus) {
    return <LoadingState message="Synthesizing today's highest-leverage actions..." />;
  }

  const primaryItem = dailyFocus?.primaryFocus;
  const supportingItems = dailyFocus?.items ? dailyFocus.items.slice(1) : [];

  const focusDataForCard = primaryItem
    ? {
        task: {
          id: primaryItem.task.id,
          title: primaryItem.task.title,
          completed: primaryItem.task.status === 'COMPLETED',
          duration: primaryItem.task.estimatedMinutes
            ? `${primaryItem.task.estimatedMinutes}m`
            : primaryItem.task.dueDate
            ? `Due ${primaryItem.task.dueDate}`
            : null,
        },
        milestone: primaryItem.task.milestoneTitle
          ? { title: primaryItem.task.milestoneTitle }
          : null,
        goal: primaryItem.task.goalTitle ? { title: primaryItem.task.goalTitle } : null,
        whyReason: primaryItem.primaryReason,
        nextActionLabel: 'Focus Now',
      }
    : null;

  return (
    <div className="lifeos-focus-page">
      {toastMessage && (
        <div className="lifeos-focus-toast" role="status">
          {toastMessage}
        </div>
      )}

      {/* Header */}
      <header className="lifeos-focus-page__header">
        <div className="lifeos-focus-page__header-main">
          <span className="lifeos-focus-page__date">{dateHeading}</span>
          <h1 className="lifeos-focus-page__title">Today&apos;s Focus</h1>
          <p className="lifeos-focus-page__subline">
            What you should focus on right now, with calm clarity on why it matters.
          </p>
        </div>

        <div className="lifeos-focus-page__header-actions">
          <Button variant="ghost" onClick={() => navigate('/tasks')}>
            View All Tasks →
          </Button>
          <Button variant="primary" onClick={() => setIsTaskModalOpen(true)}>
            + New Task
          </Button>
        </div>
      </header>

      {/* Error state */}
      {error && (
        <div className="lifeos-focus-error" role="alert">
          <p>{error}</p>
          <Button variant="ghost" onClick={fetchDailyFocus}>
            Retry
          </Button>
        </div>
      )}

      {/* All completed state */}
      {!error && dailyFocus?.allCompleted && (
        <div className="lifeos-focus-completed-banner">
          <div className="lifeos-focus-completed-icon">✓</div>
          <h2>Today is Complete</h2>
          <p>You have accomplished your planned actions for today. Take time to pause or plan ahead.</p>
          <Button variant="ghost" onClick={() => navigate('/tasks')}>
            Review Backlog
          </Button>
        </div>
      )}

      {/* Empty state */}
      {!error && !dailyFocus?.allCompleted && !primaryItem && (
        <EmptyState
          title="Nothing urgent needs your attention"
          message="Your focus will sharpen as you schedule tasks, advance milestones, and define deadlines."
          actionLabel="+ Create a Task"
          onAction={() => setIsTaskModalOpen(true)}
        />
      )}

      {/* Active Data: Signature Primary Focus + Supporting Focus */}
      {!error && primaryItem && (
        <div className="lifeos-focus-grid">
          {/* Dominant Primary Focus Anchor */}
          <section className="lifeos-focus-primary-section" aria-labelledby="primary-focus-heading">
            <span className="lifeos-kicker">HIGHEST LEVERAGE MOVE</span>
            <PrimaryFocusCard
              focusData={focusDataForCard}
              onToggleComplete={(taskId) => handleToggleComplete(taskId)}
              onStartAction={(taskId) => {
                showToast('Focus session started.');
              }}
            />
          </section>

          {/* Supporting Actions (Items 2-5) */}
          {supportingItems.length > 0 && (
            <section className="lifeos-focus-supporting-section" aria-label="Supporting Focus">
              <div className="lifeos-section-header">
                <span className="lifeos-kicker">SUPPORTING PRIORITIES</span>
                <span className="lifeos-supporting-count">{supportingItems.length} more today</span>
              </div>

              <div className="lifeos-supporting-list" role="list">
                {supportingItems.map((item) => {
                  const task = item.task;
                  const isCompleted = task.status === 'COMPLETED';
                  const isOverdue = task.status === 'OVERDUE';

                  return (
                    <div
                      key={task.id}
                      role="listitem"
                      className={`lifeos-supporting-item ${
                        isCompleted ? 'lifeos-supporting-item--completed' : ''
                      } ${isOverdue ? 'lifeos-supporting-item--overdue' : ''}`}
                    >
                      <button
                        type="button"
                        className={`lifeos-supporting-item__checkbox ${
                          isCompleted ? 'lifeos-supporting-item__checkbox--checked' : ''
                        }`}
                        onClick={() => handleToggleComplete(task.id)}
                        aria-label={`Complete task: ${task.title}`}
                      >
                        {isCompleted && (
                          <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round">
                            <polyline points="20 6 9 17 4 12" />
                          </svg>
                        )}
                      </button>

                      <div className="lifeos-supporting-item__body">
                        <div className="lifeos-supporting-item__top">
                          <span className="lifeos-supporting-item__title">{task.title}</span>
                          <span className="lifeos-supporting-item__score-pill">
                            Score {item.totalScore}
                          </span>
                        </div>

                        <div className="lifeos-supporting-item__why">
                          <span className="lifeos-why-indicator">Why:</span> {item.primaryReason}
                        </div>

                        <div className="lifeos-supporting-item__meta">
                          {task.dueDate && (
                            <span className={isOverdue ? 'lifeos-overdue-tag' : ''}>
                              📅 {task.dueDate}
                            </span>
                          )}
                          {task.estimatedMinutes && <span>⏱ {task.estimatedMinutes}m</span>}
                          {task.goalTitle && (
                            <span className="lifeos-lineage-link">
                              🎯 {task.goalTitle}
                            </span>
                          )}
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </section>
          )}
        </div>
      )}

      {/* Task Creation Modal */}
      <TaskModal
        isOpen={isTaskModalOpen}
        onClose={() => setIsTaskModalOpen(false)}
        onSubmit={handleCreateTask}
        isSubmitting={isSubmitting}
      />
    </div>
  );
};

export default FocusPage;
