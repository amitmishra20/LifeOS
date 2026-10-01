import React from 'react';
import { useNavigate } from 'react-router-dom';
import Badge from '../ui/Badge';
import './GoalHealthItemCard.css';

const HEALTH_CONFIG = {
  ON_TRACK: {
    label: 'On Track',
    badgeVariant: 'emerald',
    className: 'lifeos-gh-card--on-track',
  },
  AT_RISK: {
    label: 'At Risk',
    badgeVariant: 'warm',
    className: 'lifeos-gh-card--at-risk',
  },
  BEHIND: {
    label: 'Behind Schedule',
    badgeVariant: 'crimson',
    className: 'lifeos-gh-card--behind',
  },
  COMPLETED: {
    label: 'Completed',
    badgeVariant: 'accent',
    className: 'lifeos-gh-card--completed',
  },
};

const CATEGORY_LABELS = {
  CAREER: 'Career',
  HEALTH: 'Health',
  PERSONAL: 'Personal',
  FINANCE: 'Finance',
  EDUCATION: 'Education',
  OTHER: 'Other',
};

export const GoalHealthItemCard = ({ evaluation }) => {
  const navigate = useNavigate();

  if (!evaluation) return null;

  const {
    goalId,
    goalTitle,
    category,
    status,
    health,
    actualProgress = 0,
    expectedProgress = 0,
    delta = 0,
    startDate,
    targetDate,
    effectiveStartDate,
    totalDays,
    elapsedDays,
    remainingDays,
    reason,
  } = evaluation;

  const healthMeta = HEALTH_CONFIG[health] || HEALTH_CONFIG.ON_TRACK;

  const formatDelta = (d) => {
    if (d > 0) return `+${d.toFixed(1)}% Ahead`;
    if (d === 0) return 'On Target';
    return `${d.toFixed(1)}% Gap`;
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return 'Not set';
    return new Date(dateStr).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  };

  return (
    <div
      role="button"
      tabIndex={0}
      className={`lifeos-gh-card ${healthMeta.className}`}
      onClick={() => navigate(`/goals/${goalId}`)}
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault();
          navigate(`/goals/${goalId}`);
        }
      }}
      aria-label={`Goal health: ${goalTitle}`}
    >
      <div className="lifeos-gh-card__header">
        <div className="lifeos-gh-card__tags">
          <Badge variant="neutral" size="sm">
            <span>{CATEGORY_LABELS[category] || category}</span>
          </Badge>
          <Badge variant={healthMeta.badgeVariant} size="sm">
            <span>{healthMeta.label}</span>
          </Badge>
          {status && status !== 'ACTIVE' && (
            <Badge variant="default" size="sm">
              <span>{status}</span>
            </Badge>
          )}
        </div>
        <div className="lifeos-gh-card__delta-tag">
          <span className={`lifeos-gh-card__delta-val ${delta < -5 ? 'lifeos-gh-card__delta-val--neg' : ''}`}>
            {formatDelta(delta)}
          </span>
        </div>
      </div>

      <div className="lifeos-gh-card__body">
        <h3 className="lifeos-gh-card__title">{goalTitle}</h3>
        {reason && (
          <div className="lifeos-gh-card__reason">
            <span className="lifeos-gh-card__reason-icon">💡</span>
            <p className="lifeos-gh-card__reason-text">{reason}</p>
          </div>
        )}
      </div>

      <div className="lifeos-gh-card__progress-section">
        <div className="lifeos-gh-card__progress-bars">
          {/* Actual Progress Bar */}
          <div className="lifeos-gh-card__bar-group">
            <div className="lifeos-gh-card__bar-labels">
              <span className="lifeos-gh-card__bar-name">Actual Progress</span>
              <span className="lifeos-gh-card__bar-val">{actualProgress.toFixed(1)}%</span>
            </div>
            <div className="lifeos-gh-card__track">
              <div
                className="lifeos-gh-card__fill lifeos-gh-card__fill--actual"
                style={{ width: `${Math.min(100, Math.max(0, actualProgress))}%` }}
              />
            </div>
          </div>

          {/* Expected Progress Bar */}
          {health !== 'COMPLETED' && targetDate && (
            <div className="lifeos-gh-card__bar-group">
              <div className="lifeos-gh-card__bar-labels">
                <span className="lifeos-gh-card__bar-name">Expected Progress</span>
                <span className="lifeos-gh-card__bar-val">{expectedProgress.toFixed(1)}%</span>
              </div>
              <div className="lifeos-gh-card__track">
                <div
                  className="lifeos-gh-card__fill lifeos-gh-card__fill--expected"
                  style={{ width: `${Math.min(100, Math.max(0, expectedProgress))}%` }}
                />
              </div>
            </div>
          )}
        </div>
      </div>

      <div className="lifeos-gh-card__footer">
        <div className="lifeos-gh-card__timeline-stat">
          <span className="lifeos-gh-card__timeline-label">Start</span>
          <span className="lifeos-gh-card__timeline-value">
            {formatDate(startDate || effectiveStartDate)}
          </span>
        </div>
        <div className="lifeos-gh-card__timeline-stat">
          <span className="lifeos-gh-card__timeline-label">Target</span>
          <span className="lifeos-gh-card__timeline-value">{formatDate(targetDate)}</span>
        </div>
        <div className="lifeos-gh-card__timeline-stat">
          <span className="lifeos-gh-card__timeline-label">Remaining</span>
          <span className="lifeos-gh-card__timeline-value">
            {remainingDays !== null
              ? remainingDays < 0
                ? `${Math.abs(remainingDays)}d overdue`
                : `${remainingDays}d`
              : 'No deadline'}
          </span>
        </div>
      </div>
    </div>
  );
};

export default GoalHealthItemCard;
