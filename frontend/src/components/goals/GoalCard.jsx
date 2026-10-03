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

export const GoalCard = ({ goal, onClick }) => {
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
    : 'No horizon set';

  return (
    <div
      role="button"
      tabIndex={0}
      className={`lifeos-horizon-card lifeos-horizon-card--${status.toLowerCase()}`}
      onClick={() => onClick && onClick(id)}
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault();
          if (onClick) onClick(id);
        }
      }}
      aria-label={`Goal: ${title}`}
    >
      <div className="lifeos-horizon-card__backdrop" />
      <div className="lifeos-horizon-card__content">
      <div className="lifeos-horizon-card__header">
        <div className="lifeos-horizon-card__category-badge">
          <span className="lifeos-horizon-card__cat-dot" />
          <span>{CATEGORY_LABELS[category] || category}</span>
        </div>

        <div className="lifeos-horizon-card__status-wrap">
          {health && HEALTH_BADGES[health] && (
            <span className={`lifeos-health-pill lifeos-health-pill--${HEALTH_BADGES[health].variant}`}>
              {HEALTH_BADGES[health].label}
            </span>
          )}
          <span className={`lifeos-status-pill lifeos-status-pill--${status.toLowerCase()}`}>
            {status}
          </span>
        </div>
      </div>

      <div className="lifeos-horizon-card__body">
        <h3 className="lifeos-horizon-card__title">{title}</h3>
        {description && <p className="lifeos-horizon-card__desc">{description}</p>}
      </div>

      {/* Visual Horizon Trajectory */}
      <div className="lifeos-horizon-trajectory">
        <div className="lifeos-horizon-trajectory__bar">
          <div
            className="lifeos-horizon-trajectory__fill"
            style={{ width: `${Math.min(100, Math.max(0, progress))}%` }}
          />
        </div>
        <div className="lifeos-horizon-trajectory__stats">
          <span className="lifeos-horizon-milestones">
            {milestoneCount === 0
              ? 'No waypoints mapped'
              : `${completedMilestoneCount} of ${milestoneCount} waypoints completed`}
          </span>
          <span className="lifeos-horizon-pct">{progress}%</span>
        </div>
      </div>

      <div className="lifeos-horizon-card__footer">
        <div className="lifeos-horizon-summit-date">
          <span className="lifeos-summit-icon">⛰</span>
          <span className="lifeos-summit-label">Arrival:</span>
          <span className="lifeos-summit-value">{formattedTargetDate}</span>
        </div>

        <span className="lifeos-horizon-arrow" aria-hidden="true">
          Explore Journey →
        </span>
      </div>
      </div>
    </div>
  );
};


export default GoalCard;
