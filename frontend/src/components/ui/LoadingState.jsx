import React from 'react';
import './LoadingState.css';

/**
 * Calm Skeleton Loading States for LifeOS
 */
export const Skeleton = ({
  width = '100%',
  height = '16px',
  borderRadius = 'var(--radius-sm)',
  className = '',
}) => {
  return (
    <div
      className={`lifeos-skeleton ${className}`}
      style={{ width, height, borderRadius }}
      aria-hidden="true"
    />
  );
};

export const CardSkeleton = () => (
  <div className="lifeos-card-skeleton">
    <div className="lifeos-card-skeleton__header">
      <Skeleton width="40%" height="20px" />
      <Skeleton width="18%" height="16px" borderRadius="var(--radius-pill)" />
    </div>
    <Skeleton width="85%" height="14px" />
    <Skeleton width="65%" height="14px" />
    <div className="lifeos-card-skeleton__footer">
      <Skeleton width="30%" height="12px" />
      <Skeleton width="20%" height="28px" borderRadius="var(--radius-md)" />
    </div>
  </div>
);

export const ListSkeleton = ({ rows = 3 }) => (
  <div className="lifeos-list-skeleton">
    {Array.from({ length: rows }).map((_, idx) => (
      <div key={idx} className="lifeos-list-skeleton__row">
        <Skeleton width="20px" height="20px" borderRadius="var(--radius-sm)" />
        <div className="lifeos-list-skeleton__content">
          <Skeleton width="60%" height="16px" />
          <Skeleton width="35%" height="12px" />
        </div>
      </div>
    ))}
  </div>
);

export default Skeleton;
