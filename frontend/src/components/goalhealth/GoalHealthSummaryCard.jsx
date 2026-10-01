import React from 'react';
import './GoalHealthSummaryCard.css';

export const GoalHealthSummaryCard = ({
  totalGoals = 0,
  onTrackCount = 0,
  atRiskCount = 0,
  behindCount = 0,
  completedCount = 0,
  selectedFilter = 'ALL',
  onSelectFilter,
}) => {
  const cards = [
    {
      key: 'ALL',
      label: 'Total Goals',
      count: totalGoals,
      variant: 'default',
    },
    {
      key: 'ON_TRACK',
      label: 'On Track',
      count: onTrackCount,
      variant: 'on-track',
    },
    {
      key: 'AT_RISK',
      label: 'At Risk',
      count: atRiskCount,
      variant: 'at-risk',
    },
    {
      key: 'BEHIND',
      label: 'Behind Schedule',
      count: behindCount,
      variant: 'behind',
    },
    {
      key: 'COMPLETED',
      label: 'Completed',
      count: completedCount,
      variant: 'completed',
    },
  ];

  return (
    <div className="lifeos-gh-summary-grid">
      {cards.map((c) => {
        const isSelected = selectedFilter === c.key;
        return (
          <button
            key={c.key}
            type="button"
            className={`lifeos-gh-summary-card lifeos-gh-summary-card--${c.variant} ${
              isSelected ? 'lifeos-gh-summary-card--selected' : ''
            }`}
            onClick={() => onSelectFilter && onSelectFilter(c.key)}
            aria-pressed={isSelected}
          >
            <div className="lifeos-gh-summary-card__header">
              <span className="lifeos-gh-summary-card__label">{c.label}</span>
              <span className="lifeos-gh-summary-card__dot" />
            </div>
            <div className="lifeos-gh-summary-card__count">{c.count}</div>
            <div className="lifeos-gh-summary-card__pct">
              {totalGoals > 0 && c.key !== 'ALL'
                ? `${Math.round((c.count / totalGoals) * 100)}% of goals`
                : 'All tracked'}
            </div>
          </button>
        );
      })}
    </div>
  );
};

export default GoalHealthSummaryCard;
