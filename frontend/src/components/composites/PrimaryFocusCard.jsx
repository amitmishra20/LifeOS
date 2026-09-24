import React from 'react';
import './PrimaryFocusCard.css';

/**
 * PrimaryFocusCard Composite
 * The singular dominant visual anchor for Today's Focus.
 * Communicates: WHAT, WHY, WHEN, NEXT ACTION, and its link to Milestone & Goal.
 */
export const PrimaryFocusCard = ({
  focusData,
  onToggleComplete,
  onStartAction,
}) => {
  if (!focusData || !focusData.task) return null;

  const { task, milestone, goal, whyReason, nextActionLabel } = focusData;

  return (
    <article className={`lifeos-primary-focus-card ${task.completed ? 'lifeos-primary-focus-card--completed' : ''}`}>
      {/* Top Anchor Header */}
      <div className="lifeos-primary-focus-card__anchor-header">
        <div className="lifeos-anchor-badge-group">
          <span className="lifeos-focus-anchor-tag">TODAY</span>
          <span className="lifeos-focus-tag-divider">·</span>
          <span className="lifeos-focus-anchor-subtag">YOUR NEXT IMPORTANT MOVE</span>
        </div>
        {task.duration && (
          <span className="lifeos-focus-duration-pill">{task.duration} · High impact</span>
        )}
      </div>

      {/* Primary Action Title (WHAT) */}
      <h3 className="lifeos-primary-focus-card__title">
        {task.title}
      </h3>

      {/* Relational Breadcrumb: Milestone -> Destination Goal */}
      {(milestone || goal) && (
        <div className="lifeos-primary-focus-card__lineage">
          {milestone && <span className="lifeos-focus-lineage-milestone">{milestone.title}</span>}
          {milestone && goal && <span className="lifeos-focus-lineage-arrow">↓</span>}
          {goal && <span className="lifeos-focus-lineage-goal">{goal.title}</span>}
        </div>
      )}

      {/* Contextual Rationale (WHY) */}
      {whyReason && (
        <p className="lifeos-primary-focus-card__why">
          {whyReason}
        </p>
      )}

      {/* Action Footer (NEXT ACTION + TOGGLE) */}
      <div className="lifeos-primary-focus-card__footer">
        <button
          type="button"
          className="lifeos-primary-focus-card__action-btn"
          onClick={() => onStartAction && onStartAction(task.id)}
        >
          <span>{nextActionLabel}</span>
          <span className="lifeos-action-btn__arrow">→</span>
        </button>

        <button
          type="button"
          className={`lifeos-primary-focus-card__complete-btn ${
            task.completed ? 'lifeos-primary-focus-card__complete-btn--checked' : ''
          }`}
          onClick={() => onToggleComplete && onToggleComplete(task.id)}
          aria-pressed={task.completed}
        >
          <span className="lifeos-complete-btn__check-icon">✓</span>
          <span>{task.completed ? 'Completed' : 'Complete'}</span>
        </button>
      </div>
    </article>
  );
};

export default PrimaryFocusCard;
