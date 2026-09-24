import React from 'react';
import './GoalSummaryRow.css';

/**
 * GoalSummaryRow Composite
 * Displays strategic goal as a visual destination with milestone movement chain.
 */
export const GoalSummaryRow = ({ goal, onSelect }) => {
  if (!goal) return null;

  const { title, icon, milestonesSummary, milestoneChain = [], healthStatus, isAtRisk } = goal;

  return (
    <div
      role="button"
      tabIndex={0}
      className="lifeos-goal-summary-row"
      onClick={() => onSelect && onSelect(goal)}
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault();
          onSelect && onSelect(goal);
        }
      }}
      aria-label={`Goal: ${title}, ${milestonesSummary}`}
    >
      <div className="lifeos-goal-summary-icon" aria-hidden="true">
        {icon === 'code' && '⚡'}
        {icon === 'heart' && '❤️'}
        {icon === 'education' && '🎓'}
        {!['code', 'heart', 'education'].includes(icon) && '🎯'}
      </div>

      <div className="lifeos-goal-summary-details">
        <div className="lifeos-goal-title-row">
          <span className="lifeos-goal-summary-title" title={title}>{title}</span>
          <span className={`lifeos-health-pill ${isAtRisk ? 'lifeos-health-pill--risk' : 'lifeos-health-pill--ontrack'}`}>
            {healthStatus === 'ON_TRACK' ? 'On Track' : 'Attention'}
          </span>
        </div>
        
        {/* Visual Movement Milestone Chain */}
        <div className="lifeos-goal-milestone-chain">
          <span className="lifeos-chain-label">NOW</span>
          <div className="lifeos-chain-track">
            {milestoneChain.map((m, idx) => (
              <div
                key={m.id || idx}
                className={`lifeos-chain-node ${m.completed ? 'lifeos-chain-node--completed' : ''} ${m.active ? 'lifeos-chain-node--active' : ''}`}
                title={`${m.title} (${m.completed ? 'Completed' : m.active ? 'Active' : 'Upcoming'})`}
              />
            ))}
          </div>
          <span className="lifeos-chain-label lifeos-chain-label--dest">DESTINATION</span>
          <span className="lifeos-goal-summary-meta">{milestonesSummary}</span>
        </div>
      </div>

      <span className="lifeos-goal-chevron" aria-hidden="true">›</span>
    </div>
  );
};

export default GoalSummaryRow;
