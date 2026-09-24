import React from 'react';
import PrimaryFocusCard from '../composites/PrimaryFocusCard';
import TaskItem from '../composites/TaskItem';
import './TodayFocusSection.css';

/**
 * TodayFocusSection Experience Component
 * The operational center of gravity for the user's day.
 * Manages Dominant Anchor + Receded Supporting Actions + Completed Lifecycle.
 */
export const TodayFocusSection = ({
  focusData,
  lifeState,
  onToggleTask,
  onStartAction,
  onCreateGoal,
}) => {
  const { primaryFocus, supportingTasks, hasTasks } = focusData;
  const { stateMode } = lifeState;

  // Lifecycle State A: Empty / New Trajectory
  if (!hasTasks) {
    return (
      <section className="lifeos-focus-section lifeos-focus-section--new-user" aria-label="Today's Focus">
        <div className="lifeos-focus-invitation-surface">
          <div className="lifeos-focus-invitation-halo" aria-hidden="true" />
          <span className="lifeos-focus-invitation-badge">YOUR NEXT CHAPTER</span>
          <h2 className="lifeos-focus-invitation-title">What matters right now?</h2>
          <p className="lifeos-focus-invitation-desc">
            Define your strategic goals and milestones to illuminate your daily focus actions.
          </p>
          <button 
            type="button" 
            className="lifeos-focus-invitation-btn"
            onClick={onCreateGoal}
          >
            <span>+ Create Your First Goal</span>
          </button>
        </div>
      </section>
    );
  }

  // Lifecycle State B: ALL COMPLETED (Serene Closure)
  if (stateMode === 'ALL_COMPLETED') {
    return (
      <section className="lifeos-focus-section lifeos-focus-section--completed" aria-label="Today's Focus">
        <div className="lifeos-focus-closure-surface">
          <div className="lifeos-focus-closure-badge">TODAY IS COMPLETE</div>
          <h2 className="lifeos-focus-closure-title">Rest. Reflect. Prepare what comes next.</h2>
          <p className="lifeos-focus-closure-desc">
            All scheduled milestone deliverables for today have been completed. Step back, protect your recovery, or capture tomorrow's ideas.
          </p>
          <div className="lifeos-focus-closure-list" role="list" aria-label="Completed Actions Today">
            {supportingTasks.concat(primaryFocus ? [primaryFocus.task] : []).map((t) => (
              <div key={t.id} role="listitem">
                <TaskItem task={t} onToggle={onToggleTask} isReceded />
              </div>
            ))}
          </div>
        </div>
      </section>
    );
  }

  // Standard Active State: Dominant Anchor + Supporting Stream
  return (
    <section className="lifeos-focus-section" aria-label="Today's Focus">
      {/* Section Header */}
      <div className="lifeos-section-header">
        <div className="lifeos-section-header__left">
          <h2 className="lifeos-section-header__title">Today's Focus</h2>
          <p className="lifeos-section-header__subtitle">
            Your next deliberate step brings you closer to your destination.
          </p>
        </div>
        <div className="lifeos-section-header__right">
          <span className="lifeos-date-pill">
            {supportingTasks.length + (primaryFocus ? 1 : 0)} actions scheduled
          </span>
        </div>
      </div>

      {/* Dual Layout: Dominant Anchor + Supporting Stream */}
      <div className="lifeos-focus-split">
        {/* Dominant Visual Anchor */}
        <div className="lifeos-focus-split__primary">
          <PrimaryFocusCard
            focusData={primaryFocus}
            onToggleComplete={onToggleTask}
            onStartAction={onStartAction}
          />
        </div>

        {/* Supporting Secondary Actions (Receded) */}
        <div className="lifeos-focus-split__secondary">
          <div className="lifeos-supporting-tasks-card">
            <div className="lifeos-supporting-tasks-header">
              <span className="lifeos-supporting-tasks-title">Supporting Actions</span>
              <span className="lifeos-supporting-tasks-count">{supportingTasks.length} queued</span>
            </div>

            <div className="lifeos-supporting-tasks-list" role="list">
              {supportingTasks.map((task) => (
                <div key={task.id} role="listitem">
                  <TaskItem
                    task={task}
                    onToggle={onToggleTask}
                    isReceded
                  />
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default TodayFocusSection;
