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
  message,
  action,
  actionLabel,
  actionText,
  onAction,
  className = '',
}) => {
  const descText = description || message;
  const label = actionLabel || actionText;

  return (
    <div className={`lifeos-empty-state ${className}`}>
      {icon && <div className="lifeos-empty-state__icon">{icon}</div>}
      <h3 className="lifeos-empty-state__title">{title}</h3>
      {descText && <p className="lifeos-empty-state__desc">{descText}</p>}
      {action ? (
        <div className="lifeos-empty-state__action">{action}</div>
      ) : label && onAction ? (
        <div className="lifeos-empty-state__action">
          <Button variant="secondary" size="md" onClick={onAction}>
            {label}
          </Button>
        </div>
      ) : null}
    </div>
  );
};

export default EmptyState;
