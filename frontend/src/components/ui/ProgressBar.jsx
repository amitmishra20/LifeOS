import React from 'react';
import './ProgressBar.css';

/**
 * Reusable Linear Progress Bar
 */
export const ProgressBar = ({
  value = 0,
  max = 100,
  variant = 'warm', // 'warm' | 'indigo' (alias) | 'emerald' | 'amber' | 'crimson'
  size = 'md',      // 'sm' | 'md' | 'lg'
  showLabel = false,
  label,
  className = '',
}) => {
  const percentage = Math.min(Math.max(Math.round((value / max) * 100), 0), 100);

  return (
    <div className={`lifeos-progress ${className}`}>
      {(showLabel || label) && (
        <div className="lifeos-progress__header">
          {label && <span className="lifeos-progress__label">{label}</span>}
          {showLabel && <span className="lifeos-progress__value">{percentage}%</span>}
        </div>
      )}
      <div className={`lifeos-progress__track lifeos-progress__track--${size}`}>
        <div
          className={`lifeos-progress__bar lifeos-progress__bar--${variant}`}
          style={{ width: `${percentage}%` }}
          role="progressbar"
          aria-valuenow={percentage}
          aria-valuemin="0"
          aria-valuemax="100"
        />
      </div>
    </div>
  );
};

/**
 * SVG Circular Progress Ring
 */
export const ProgressRing = ({
  value = 0,
  max = 100,
  size = 64,
  strokeWidth = 6,
  variant = 'warm',
  children,
  className = '',
}) => {
  const percentage = Math.min(Math.max(Math.round((value / max) * 100), 0), 100);
  const radius = (size - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference - (percentage / 100) * circumference;

  return (
    <div
      className={`lifeos-ring-container ${className}`}
      style={{ width: size, height: size }}
    >
      <svg
        className="lifeos-ring-svg"
        width={size}
        height={size}
        viewBox={`0 0 ${size} ${size}`}
      >
        <circle
          className="lifeos-ring-bg"
          cx={size / 2}
          cy={size / 2}
          r={radius}
          strokeWidth={strokeWidth}
        />
        <circle
          className={`lifeos-ring-progress lifeos-ring-progress--${variant}`}
          cx={size / 2}
          cy={size / 2}
          r={radius}
          strokeWidth={strokeWidth}
          strokeDasharray={circumference}
          strokeDashoffset={strokeDashoffset}
        />
      </svg>
      {children && <div className="lifeos-ring-content">{children}</div>}
    </div>
  );
};

export default ProgressBar;
