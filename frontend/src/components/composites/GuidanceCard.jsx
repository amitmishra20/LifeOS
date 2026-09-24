import React, { useState } from 'react';
import './GuidanceCard.css';

/**
 * GuidanceCard Composite
 * Quiet, editorial guidance displaying a single crisp observation,
 * actionable recommendation, and progressive disclosure for rationale.
 */
export const GuidanceCard = ({ guidance, onTriggerAction }) => {
  const [showWhy, setShowWhy] = useState(false);

  if (!guidance) return null;

  const { observation, whyItMatters, suggestion, actionLabel, actionType } = guidance;

  return (
    <div className="lifeos-guidance-card">
      <div className="lifeos-guidance-card__header">
        <span className="lifeos-guidance-card__tag">INSIGHT</span>
      </div>

      <div className="lifeos-guidance-card__content">
        <p className="lifeos-guidance-observation">{observation}</p>
        
        {suggestion && (
          <p className="lifeos-guidance-suggestion">{suggestion}</p>
        )}

        {/* Progressive Disclosure for Deep Rationale */}
        {whyItMatters && (
          <div className="lifeos-guidance-disclosure">
            <button
              type="button"
              className="lifeos-guidance-disclosure-btn"
              onClick={() => setShowWhy(!showWhy)}
              aria-expanded={showWhy}
            >
              <span>{showWhy ? 'Hide rationale ▴' : 'Why this matters ▾'}</span>
            </button>
            {showWhy && (
              <p className="lifeos-guidance-why-expanded">{whyItMatters}</p>
            )}
          </div>
        )}

        {actionLabel && (
          <button
            type="button"
            className="lifeos-guidance-action-btn"
            onClick={() => onTriggerAction && onTriggerAction(actionType)}
          >
            <span>{actionLabel}</span>
            <span className="lifeos-guidance-btn-arrow">→</span>
          </button>
        )}
      </div>
    </div>
  );
};

export default GuidanceCard;
