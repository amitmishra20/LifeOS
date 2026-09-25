import React from 'react';
import Badge from '../ui/Badge';
import Button from '../ui/Button';
import EmptyState from '../ui/EmptyState';
import './MilestoneList.css';

const STATUS_CONFIG = {
  PENDING: { label: 'Pending', badge: 'neutral', next: 'ACTIVE' },
  ACTIVE: { label: 'Active', badge: 'accent', next: 'COMPLETED' },
  COMPLETED: { label: 'Completed', badge: 'emerald', next: 'PENDING' },
};

export const MilestoneList = ({
  milestones = [],
  onAddMilestone,
  onEditMilestone,
  onDeleteMilestone,
  onToggleStatus,
}) => {
  if (!milestones || milestones.length === 0) {
    return (
      <div className="lifeos-milestones-empty-wrap">
        <EmptyState
          icon={
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10" />
              <polyline points="12 6 12 12 14 14" />
            </svg>
          }
          title="No milestones established yet."
          description="Break this goal into meaningful phases to make the path forward clearer."
          action={
            <Button variant="primary" size="md" onClick={onAddMilestone}>
              + Add First Milestone
            </Button>
          }
        />
      </div>
    );
  }

  return (
    <div className="lifeos-milestone-list">
      <div className="lifeos-milestone-list__header">
        <div className="lifeos-milestone-list__title-group">
          <h3 className="lifeos-milestone-list__title">Phased Milestones</h3>
          <span className="lifeos-milestone-list__count">
            {milestones.filter((m) => m.status === 'COMPLETED').length} of {milestones.length} completed
          </span>
        </div>
        <Button variant="secondary" size="sm" onClick={onAddMilestone}>
          + Add Milestone
        </Button>
      </div>

      <div className="lifeos-milestone-list__items">
        {milestones.map((milestone, idx) => {
          const config = STATUS_CONFIG[milestone.status] || STATUS_CONFIG.PENDING;
          const isCompleted = milestone.status === 'COMPLETED';
          const isActive = milestone.status === 'ACTIVE';

          const formattedDate = milestone.targetDate
            ? new Date(milestone.targetDate).toLocaleDateString('en-US', {
                month: 'short',
                day: 'numeric',
                year: 'numeric',
              })
            : null;

          return (
            <div
              key={milestone.id}
              className={`lifeos-milestone-row ${isCompleted ? 'lifeos-milestone-row--completed' : ''} ${
                isActive ? 'lifeos-milestone-row--active' : ''
              }`}
            >
              {/* Order index / Completion check toggle */}
              <button
                type="button"
                className={`lifeos-milestone-check ${isCompleted ? 'lifeos-milestone-check--checked' : ''} ${
                  isActive ? 'lifeos-milestone-check--active' : ''
                }`}
                onClick={() => onToggleStatus && onToggleStatus(milestone)}
                title={`Status: ${config.label}. Click to mark as ${
                  isCompleted ? 'Pending' : 'Completed'
                }`}
                aria-label={`Toggle completion for ${milestone.title}`}
              >
                {isCompleted ? (
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
                    <polyline points="20 6 9 17 4 12" />
                  </svg>
                ) : (
                  <span className="lifeos-milestone-index">{idx + 1}</span>
                )}
              </button>

              {/* Main content */}
              <div className="lifeos-milestone-row__content">
                <div className="lifeos-milestone-row__top">
                  <span className="lifeos-milestone-row__title">{milestone.title}</span>
                  <button
                    type="button"
                    className="lifeos-milestone-status-btn"
                    onClick={() => onToggleStatus && onToggleStatus(milestone)}
                    title="Click to cycle status"
                  >
                    <Badge variant={config.badge} size="sm">
                      <span>{config.label}</span>
                    </Badge>
                  </button>
                </div>

                {milestone.description && (
                  <p className="lifeos-milestone-row__desc">{milestone.description}</p>
                )}

                {formattedDate && (
                  <div className="lifeos-milestone-row__meta">
                    <span className="lifeos-milestone-row__meta-label">Target:</span>
                    <span className="lifeos-milestone-row__meta-val">{formattedDate}</span>
                  </div>
                )}
              </div>

              {/* Actions */}
              <div className="lifeos-milestone-row__actions">
                <button
                  type="button"
                  className="lifeos-icon-btn"
                  onClick={() => onEditMilestone && onEditMilestone(milestone)}
                  title="Edit Milestone"
                  aria-label={`Edit ${milestone.title}`}
                >
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M12 20h9" />
                    <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z" />
                  </svg>
                </button>
                <button
                  type="button"
                  className="lifeos-icon-btn lifeos-icon-btn--danger"
                  onClick={() => onDeleteMilestone && onDeleteMilestone(milestone)}
                  title="Delete Milestone"
                  aria-label={`Delete ${milestone.title}`}
                >
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <polyline points="3 6 5 6 21 6" />
                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                  </svg>
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default MilestoneList;
