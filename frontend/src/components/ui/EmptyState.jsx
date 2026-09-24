import React from 'react';
import Button from './Button';
import './EmptyState.css';

/**
 * Purposeful LifeOS Empty State
 * Answers: 1. What is missing? 2. Why it matters? 3. What the user can do next?
 */
export const EmptyState = ({
  icon,
  title = 'No items found',
  description,
  actionLabel,
  onAction,
  className = '',
}) => {
  return (
    <div className={`lifeos-empty-state ${className}`}>
      {icon && <div className="lifeos-empty-state__icon">{icon}</div>}
      <h3 className="lifeos-empty-state__title">{title}</h3>
      {description && <p className="lifeos-empty-state__desc">{description}</p>}
      {actionLabel && onAction && (
        <div className="lifeos-empty-state__action">
          <Button variant="secondary" size="sm" onClick={onAction}>
            {actionLabel}
          </Button>
        </div>
      )}
    </div>
  );
};

export default EmptyState;
