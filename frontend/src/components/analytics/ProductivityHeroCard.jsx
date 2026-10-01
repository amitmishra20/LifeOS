import React from 'react';
import './ProductivityHeroCard.css';

const COMPONENT_LABELS = {
  taskCompletion: 'Task Completion',
  habitConsistency: 'Habit Consistency',
  goalProgress: 'Goal Progress',
  learningActivity: 'Learning Activity',
};

export const ProductivityHeroCard = ({ productivity }) => {
  if (!productivity) return null;

  const { score = 0, weights = {}, components = {}, activeDomainCount = 0, totalDomainCount = 4 } = productivity;

  const componentKeys = ['taskCompletion', 'habitConsistency', 'goalProgress', 'learningActivity'];

  return (
    <div className="lifeos-productivity-hero" data-testid="productivity-hero">
      <div className="lifeos-productivity-hero__header">
        <div className="lifeos-productivity-hero__title-wrap">
          <h2 className="lifeos-productivity-hero__title">System Productivity Score</h2>
          <p className="lifeos-productivity-hero__subtitle">
            Weighted synthesis across your active LifeOS domains
          </p>
        </div>
        <div className="lifeos-productivity-hero__badge">
          <span>●</span>
          <span>
            {activeDomainCount} of {totalDomainCount} domains active
          </span>
        </div>
      </div>

      <div className="lifeos-productivity-hero__content">
        <div className="lifeos-productivity-hero__score-dial">
          <div className="lifeos-productivity-hero__score-value">
            {Number(score).toFixed(1)}
            <span className="lifeos-productivity-hero__score-unit">%</span>
          </div>
          <span className="lifeos-productivity-hero__score-label">Current Score</span>
        </div>

        <div className="lifeos-productivity-hero__components-grid">
          {componentKeys.map((key) => {
            const comp = components[key] || {
              score: 0,
              weight: weights[key] || 0,
              contribution: 0,
              active: false,
            };
            const label = COMPONENT_LABELS[key] || key;
            const weightPercent = Math.round((comp.weight || weights[key] || 0) * 100);

            return (
              <div
                key={key}
                className={`lifeos-productivity-hero__component-item ${
                  !comp.active ? 'lifeos-productivity-hero__comp-item--inactive' : ''
                }`}
              >
                <div className="lifeos-productivity-hero__comp-head">
                  <span className="lifeos-productivity-hero__comp-name">{label}</span>
                  <span className="lifeos-productivity-hero__comp-weight">{weightPercent}% wt</span>
                </div>

                <div className="lifeos-productivity-hero__comp-stats">
                  <span className="lifeos-productivity-hero__comp-score">
                    {Number(comp.score || 0).toFixed(1)}%
                  </span>
                  <span className="lifeos-productivity-hero__comp-contrib">
                    +{Number(comp.contribution || 0).toFixed(1)} pts
                  </span>
                </div>

                <div className="lifeos-productivity-hero__bar-bg">
                  <div
                    className="lifeos-productivity-hero__bar-fill"
                    style={{ width: `${Math.min(100, Math.max(0, comp.score || 0))}%` }}
                  />
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default ProductivityHeroCard;
