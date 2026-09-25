import React, { useState } from 'react';
import Badge from '../ui/Badge';
import './LifeMap.css';

export const LifeMap = ({
  goal,
  milestones = [],
  onSelectMilestone,
}) => {
  const [selectedMilestoneId, setSelectedMilestoneId] = useState(null);

  if (!goal) return null;

  const sortedMilestones = [...milestones].sort(
    (a, b) => (a.orderIndex ?? 0) - (b.orderIndex ?? 0)
  );

  const startDateFormatted = goal.startDate
    ? new Date(goal.startDate).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
      })
    : 'Inception';

  const targetDateFormatted = goal.targetDate
    ? new Date(goal.targetDate).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
      })
    : 'Arrival';

  const activeMilestone = sortedMilestones.find((m) => m.id === selectedMilestoneId);

  const handleWaypointClick = (m) => {
    setSelectedMilestoneId((prev) => (prev === m.id ? null : m.id));
    if (onSelectMilestone) onSelectMilestone(m);
  };

  return (
    <div className="lifeos-lifemap">
      <div className="lifeos-lifemap__header">
        <div className="lifeos-lifemap__title-block">
          <span className="lifeos-lifemap__eyebrow">Strategic Progression</span>
          <h3 className="lifeos-lifemap__heading">Life Map</h3>
        </div>
        <div className="lifeos-lifemap__legend">
          <span className="lifeos-lifemap__legend-item lifeos-lifemap__legend-item--completed">
            <span className="lifeos-lifemap__legend-dot" /> Completed
          </span>
          <span className="lifeos-lifemap__legend-item lifeos-lifemap__legend-item--active">
            <span className="lifeos-lifemap__legend-dot" /> Current Focus
          </span>
          <span className="lifeos-lifemap__legend-item lifeos-lifemap__legend-item--pending">
            <span className="lifeos-lifemap__legend-dot" /> Ahead
          </span>
        </div>
      </div>

      {sortedMilestones.length === 0 ? (
        <div className="lifeos-lifemap__empty-track">
          <div className="lifeos-lifemap__empty-stem" />
          <p className="lifeos-lifemap__empty-text">
            Add milestones to visualize the spatial journey toward this summit.
          </p>
        </div>
      ) : (
        <div className="lifeos-lifemap__spatial-viewport">
          <div className="lifeos-lifemap__track">
            {/* Origin Checkpoint */}
            <div className="lifeos-lifemap__node lifeos-lifemap__node--origin">
              <div className="lifeos-lifemap__node-marker">
                <span className="lifeos-lifemap__origin-dot" />
              </div>
              <div className="lifeos-lifemap__node-content">
                <span className="lifeos-lifemap__node-tag">Origin</span>
                <span className="lifeos-lifemap__node-date">{startDateFormatted}</span>
              </div>
            </div>

            {/* Sequential Milestones */}
            {sortedMilestones.map((m, idx) => {
              const statusClass = (m.status || 'PENDING').toLowerCase();
              const isSelected = selectedMilestoneId === m.id;
              const isCompleted = m.status === 'COMPLETED';
              const isActive = m.status === 'ACTIVE';

              const mDate = m.targetDate
                ? new Date(m.targetDate).toLocaleDateString('en-US', {
                    month: 'short',
                    day: 'numeric',
                  })
                : null;

              return (
                <React.Fragment key={m.id}>
                  {/* Connecting Spine Segment */}
                  <div
                    className={`lifeos-lifemap__spine ${
                      isCompleted ? 'lifeos-lifemap__spine--completed' : ''
                    }`}
                  />

                  {/* Milestone Node */}
                  <div
                    className={`lifeos-lifemap__node lifeos-lifemap__node--milestone lifeos-lifemap__node--${statusClass} ${
                      isSelected ? 'lifeos-lifemap__node--selected' : ''
                    }`}
                    onClick={() => handleWaypointClick(m)}
                    role="button"
                    tabIndex={0}
                    aria-label={`Milestone: ${m.title} (${m.status})`}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter' || e.key === ' ') {
                        e.preventDefault();
                        handleWaypointClick(m);
                      }
                    }}
                  >
                    <div className="lifeos-lifemap__node-marker">
                      {isActive && <span className="lifeos-lifemap__pulse" aria-hidden="true" />}
                      <span className="lifeos-lifemap__waypoint-dot">
                        {isCompleted ? (
                          <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
                            <polyline points="20 6 9 17 4 12" />
                          </svg>
                        ) : (
                          idx + 1
                        )}
                      </span>
                    </div>

                    <div className="lifeos-lifemap__node-content">
                      <span className="lifeos-lifemap__node-phase">Phase {idx + 1}</span>
                      <span className="lifeos-lifemap__node-title" title={m.title}>
                        {m.title}
                      </span>
                      {mDate && <span className="lifeos-lifemap__node-date">{mDate}</span>}
                    </div>
                  </div>
                </React.Fragment>
              );
            })}

            {/* Connecting Spine to Summit */}
            <div
              className={`lifeos-lifemap__spine ${
                goal.progress === 100 ? 'lifeos-lifemap__spine--completed' : ''
              }`}
            />

            {/* Summit Destination */}
            <div className="lifeos-lifemap__node lifeos-lifemap__node--summit">
              <div className="lifeos-lifemap__node-marker">
                <span className="lifeos-lifemap__summit-flag" aria-hidden="true">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z" />
                    <line x1="4" y1="22" x2="4" y2="15" stroke="currentColor" strokeWidth="2" />
                  </svg>
                </span>
              </div>
              <div className="lifeos-lifemap__node-content">
                <span className="lifeos-lifemap__node-tag">Summit</span>
                <span className="lifeos-lifemap__node-title" title={goal.title}>
                  {goal.title}
                </span>
                <span className="lifeos-lifemap__node-date">{targetDateFormatted}</span>
              </div>
            </div>
          </div>

          {/* Interactive Context Drawer for selected milestone */}
          {activeMilestone && (
            <div className="lifeos-lifemap__context-panel" role="region" aria-label="Waypoint Context">
              <div className="lifeos-lifemap__context-header">
                <div>
                  <span className="lifeos-lifemap__context-kicker">Waypoint Context</span>
                  <h4 className="lifeos-lifemap__context-title">{activeMilestone.title}</h4>
                </div>
                <button
                  type="button"
                  className="lifeos-icon-btn"
                  onClick={() => setSelectedMilestoneId(null)}
                  aria-label="Close context"
                >
                  ✕
                </button>
              </div>

              <div className="lifeos-lifemap__context-body">
                <div className="lifeos-lifemap__context-meta">
                  <Badge
                    variant={
                      activeMilestone.status === 'COMPLETED'
                        ? 'emerald'
                        : activeMilestone.status === 'ACTIVE'
                        ? 'accent'
                        : 'neutral'
                    }
                    size="sm"
                  >
                    <span>{activeMilestone.status}</span>
                  </Badge>
                  {activeMilestone.targetDate && (
                    <span className="lifeos-lifemap__context-date">
                      Target: {new Date(activeMilestone.targetDate).toLocaleDateString()}
                    </span>
                  )}
                </div>
                {activeMilestone.description && (
                  <p className="lifeos-lifemap__context-desc">{activeMilestone.description}</p>
                )}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default LifeMap;
