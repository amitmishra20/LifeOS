import React from 'react';
import Badge from '../ui/Badge';
import ProgressBar from '../ui/ProgressBar';
import './GoalCard.css';

const CATEGORY_LABELS = {
  CAREER: 'Career',
  HEALTH: 'Health',
  PERSONAL: 'Personal',
  FINANCE: 'Finance',
  EDUCATION: 'Education',
  OTHER: 'Other',
};

const CATEGORY_BADGES = {
  CAREER: 'accent',
  HEALTH: 'emerald',
  PERSONAL: 'neutral',
  FINANCE: 'warm',
  EDUCATION: 'default',
  OTHER: 'neutral',
};

const PRIORITY_BADGES = {
  LOW: 'neutral',
  MEDIUM: 'default',
  HIGH: 'warm',
  CRITICAL: 'crimson',
};

const HEALTH_BADGES = {
  ON_TRACK: { label: 'On Track', variant: 'emerald' },
  AT_RISK: { label: 'At Risk', variant: 'warm' },
  BEHIND: { label: 'Behind', variant: 'crimson' },
  COMPLETED: { label: 'Completed', variant: 'accent' },
};

export const GoalCard = ({ goal, onClick, featured = false }) => {
  if (!goal) return null;

  const {
    id,
    title,
    description,
    category,
    priority,
    status,
    health,
    progress = 0,
    targetDate,
    milestoneCount = 0,
    completedMilestoneCount = 0,
  } = goal;

  const formattedTargetDate = targetDate
    ? new Date(targetDate).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
      })
    : null;

  return (
    <div
      role="button"
      tabIndex={0}
      className={`lifeos-goal-card lifeos-goal-card--${status.toLowerCase()} ${featured ? 'lifeos-goal-card--featured' : ''}`}
      onClick={() => onClick && onClick(id)}
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault();
          if (onClick) onClick(id);
        }
      }}
      aria-label={`Goal: ${title}`}
    >
      <div className="lifeos-goal-card__header">
        <div className="lifeos-goal-card__tags">
          <Badge variant={CATEGORY_BADGES[category] || 'default'} size="sm">
            <span>{CATEGORY_LABELS[category] || category}</span>
          </Badge>
          {priority && (
            <Badge variant={PRIORITY_BADGES[priority] || 'default'} size="sm">
              <span>{priority}</span>
            </Badge>
          )}
          {health && HEALTH_BADGES[health] && (
            <Badge variant={HEALTH_BADGES[health].variant} size="sm">
              <span>{HEALTH_BADGES[health].label}</span>
            </Badge>
          )}
        </div>
        <span className={`lifeos-goal-card__status-dot lifeos-goal-card__status-dot--${status.toLowerCase()}`} title={status}>
          {status}
        </span>
      </div>

      <div className="lifeos-goal-card__body">
        <h3 className="lifeos-goal-card__title">{title}</h3>
        {description && <p className="lifeos-goal-card__desc">{description}</p>}
      </div>

      <div className="lifeos-goal-card__footer">
        <div className="lifeos-goal-card__progress-wrap">
          <div className="lifeos-goal-card__progress-info">
            <span className="lifeos-goal-card__milestones-count">
              {milestoneCount === 0
                ? 'No milestones'
                : `${completedMilestoneCount} of ${milestoneCount} milestones`}
            </span>
            <span className="lifeos-goal-card__progress-percent">{progress}%</span>
          </div>
          <ProgressBar value={progress} size="sm" variant={progress === 100 ? 'emerald' : 'accent'} />
        </div>

        {formattedTargetDate && (
          <div className="lifeos-goal-card__date">
            <span className="lifeos-goal-card__date-label">Target</span>
            <span className="lifeos-goal-card__date-value">{formattedTargetDate}</span>
          </div>
        )}
      </div>

      <span className="lifeos-goal-card__chevron" aria-hidden="true">
        ›
      </span>
    </div>
  );
};

export default GoalCard;
