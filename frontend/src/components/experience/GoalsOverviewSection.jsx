import React from 'react';
import GoalSummaryRow from '../composites/GoalSummaryRow';
import './GoalsOverviewSection.css';

/**
 * GoalsOverviewSection Experience Component
 * Displays strategic goals, milestone ratios, and health indicators.
 */
export const GoalsOverviewSection = ({ goalsData, onSelectGoal, onCreateGoal }) => {
  if (!goalsData || !goalsData.hasGoals) {
    return (
      <section className="lifeos-goals-overview-section" aria-label="Strategic Goals">
        <div className="lifeos-goals-overview-card">
          <div className="lifeos-card-header">
            <h3 className="lifeos-card-title">Strategic Goals</h3>
          </div>
          <div className="lifeos-goals-empty">
            <p>No goals defined yet.</p>
            <button
              type="button"
              className="lifeos-goals-empty-btn"
              onClick={onCreateGoal}
            >
              Add First Goal
            </button>
          </div>
        </div>
      </section>
    );
  }

  const { goalsList } = goalsData;

  return (
    <section className="lifeos-goals-overview-section" aria-label="Strategic Goals">
      <div className="lifeos-goals-overview-card">
        <div className="lifeos-card-header">
          <h3 className="lifeos-card-title">Strategic Goals</h3>
        </div>

        <div className="lifeos-goals-list" role="list">
          {goalsList.map((goal) => (
            <div key={goal.id} role="listitem">
              <GoalSummaryRow goal={goal} onSelect={onSelectGoal} />
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};

export default GoalsOverviewSection;
