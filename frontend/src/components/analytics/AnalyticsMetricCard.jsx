import React from 'react';
import './AnalyticsMetricCard.css';

export const AnalyticsMetricCard = ({
  icon,
  title,
  mainValue,
  mainUnit = '%',
  mainLabel,
  progressPercentage,
  isActive = true,
  stats = [],
  dataTestId,
}) => {
  return (
    <div className="lifeos-metric-card" data-testid={dataTestId}>
      <div className="lifeos-metric-card__header">
        <div className="lifeos-metric-card__title-row">
          {icon && <div className="lifeos-metric-card__icon">{icon}</div>}
          <h3 className="lifeos-metric-card__title">{title}</h3>
        </div>
        <span
          className={`lifeos-metric-card__badge ${
            isActive ? 'lifeos-metric-card__badge--active' : ''
          }`}
        >
          {isActive ? 'Active' : 'No Data'}
        </span>
      </div>

      <div>
        <div className="lifeos-metric-card__main-stat">
          <span className="lifeos-metric-card__value">
            {typeof mainValue === 'number' ? mainValue.toFixed(1) : mainValue}
          </span>
          {mainUnit && <span className="lifeos-metric-card__unit">{mainUnit}</span>}
        </div>
        {mainLabel && <div className="lifeos-metric-card__label">{mainLabel}</div>}
      </div>

      {progressPercentage !== undefined && (
        <div className="lifeos-metric-card__progress-bar">
          <div
            className="lifeos-metric-card__progress-fill"
            style={{
              width: `${Math.min(100, Math.max(0, Number(progressPercentage) || 0))}%`,
            }}
          />
        </div>
      )}

      {stats && stats.length > 0 && (
        <div className="lifeos-metric-card__stats-list">
          {stats.map((s, idx) => (
            <div key={idx} className="lifeos-metric-card__stat-item">
              <span className="lifeos-metric-card__stat-num">{s.value}</span>
              <span className="lifeos-metric-card__stat-desc">{s.label}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default AnalyticsMetricCard;
