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

      {/* Editorial Header */}
      <header className="lifeos-focus-page__header">
        <div className="lifeos-focus-page__header-main">
          <div className="lifeos-focus-date-badge">
            <span className="lifeos-focus-date-dot" />
            <span className="lifeos-focus-date-text">{dateHeading}</span>
          </div>
          <h1 className="lifeos-focus-page__title">Today's Focus</h1>
          <p className="lifeos-focus-page__subline">
            A quiet decision surface. One dominant move to advance your horizon.
          </p>
        </div>

        <div className="lifeos-focus-page__header-actions">
          <Button variant="ghost" onClick={() => navigate('/tasks')}>
            <span>All Tasks</span> <span aria-hidden="true">→</span>
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

      {/* Active Data: Dominant Focal Anchor + Supporting Queue */}
      {!error && primaryItem && (
        <div className="lifeos-focus-layout">
          {/* Dominant Primary Focus Anchor */}
          <section className="lifeos-focal-anchor" aria-labelledby="primary-focus-heading">
            <div className="lifeos-focal-anchor__kicker">
              <span className="lifeos-section-num">01</span>
              <span className="lifeos-focal-anchor__rule" />
              <span>HIGHEST LEVERAGE MOVE</span>
            </div>

            <div className="lifeos-focal-card">
              <div className="lifeos-focal-card__lineage">
                {primaryItem.task.goalTitle && (
                  <span className="lifeos-lineage-node lifeos-lineage-goal">
                    <span className="lifeos-lineage-icon">🎯</span>
                    <span>{primaryItem.task.goalTitle}</span>
                  </span>
                )}
                {primaryItem.task.milestoneTitle && (
                  <>
                    <span className="lifeos-lineage-sep">›</span>
                    <span className="lifeos-lineage-node lifeos-lineage-milestone">
                      <span>{primaryItem.task.milestoneTitle}</span>
                    </span>
                  </>
                )}
              </div>

              <h2 id="primary-focus-heading" className="lifeos-focal-card__title">
                {primaryItem.task.title}
              </h2>

              <div className="lifeos-focal-card__why-box">
                <span className="lifeos-why-label">Why Now</span>
                <p className="lifeos-why-text">{primaryItem.primaryReason}</p>
              </div>

              <div className="lifeos-focal-card__footer">
                <div className="lifeos-focal-card__meta">
                  {primaryItem.task.estimatedMinutes && (
                    <span className="lifeos-meta-tag">⏱ {primaryItem.task.estimatedMinutes} min</span>
                  )}
                  {primaryItem.task.dueDate && (
                    <span className={`lifeos-meta-tag ${primaryItem.task.status === 'OVERDUE' ? 'is-overdue' : ''}`}>
                      📅 Due {primaryItem.task.dueDate}
                    </span>
                  )}
                  {primaryItem.totalScore && (
                    <span className="lifeos-meta-tag lifeos-score-tag">Score {primaryItem.totalScore}</span>
                  )}
                </div>

                <div className="lifeos-focal-card__actions">
                  <Button
                    variant={primaryItem.task.status === 'COMPLETED' ? 'secondary' : 'primary'}
                    size="md"
                    onClick={() => handleToggleComplete(primaryItem.task.id)}
                    className="lifeos-focal-complete-btn"
                  >
                    <span>{primaryItem.task.status === 'COMPLETED' ? 'Reopen Action' : 'Complete Action'}</span>
                    <span aria-hidden="true">✓</span>
                  </Button>
                </div>
              </div>
            </div>
          </section>

          {/* Supporting Priorities Queue */}
          {supportingItems.length > 0 && (
            <section className="lifeos-supporting-section" aria-label="Supporting Priorities">
              <div className="lifeos-supporting-section__header">
                <div className="lifeos-focal-anchor__kicker">
                  <span className="lifeos-section-num">02</span>
                  <span className="lifeos-focal-anchor__rule" />
                  <span>SUPPORTING PRIORITIES</span>
                </div>
                <span className="lifeos-supporting-count">{supportingItems.length} in queue</span>
              </div>

              <div className="lifeos-supporting-stream" role="list">
                {supportingItems.map((item, idx) => {
                  const task = item.task;
                  const isCompleted = task.status === 'COMPLETED';
                  const isOverdue = task.status === 'OVERDUE';

                  return (
                    <div
                      key={task.id}
                      role="listitem"
                      className={`lifeos-queue-row ${
                        isCompleted ? 'lifeos-queue-row--completed' : ''
                      } ${isOverdue ? 'lifeos-queue-row--overdue' : ''}`}
                    >
                      <button
                        type="button"
                        className={`lifeos-queue-check ${
                          isCompleted ? 'is-checked' : ''
                        }`}
                        onClick={() => handleToggleComplete(task.id)}
                        aria-label={`Mark task ${task.title} as ${isCompleted ? 'incomplete' : 'complete'}`}
                      >
                        {isCompleted && (
                          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round">
                            <polyline points="20 6 9 17 4 12" />
                          </svg>
                        )}
                      </button>

                      <div className="lifeos-queue-body">
                        <div className="lifeos-queue-headline">
                          <span className="lifeos-queue-index">0{idx + 2}</span>
                          <span className="lifeos-queue-title">{task.title}</span>
                          {task.goalTitle && (
                            <span className="lifeos-queue-goal-tag">🎯 {task.goalTitle}</span>
                          )}
                        </div>

                        <p className="lifeos-queue-why">
                          <span className="lifeos-queue-why-tag">Why:</span> {item.primaryReason}
                        </p>
                      </div>

                      <div className="lifeos-queue-meta">
                        {task.estimatedMinutes && <span className="lifeos-queue-time">{task.estimatedMinutes}m</span>}
                        {task.dueDate && <span className={`lifeos-queue-date ${isOverdue ? 'is-overdue' : ''}`}>{task.dueDate}</span>}
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
