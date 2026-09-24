import React from 'react';
import './Badge.css';

/**
 * Reusable LifeOS Badge Component
 * @param {'default' | 'indigo' | 'emerald' | 'amber' | 'crimson' | 'outline'} variant
 * @param {'xs' | 'sm' | 'md'} size
 * @param {boolean} hasDot
 * @param {React.ReactNode} children
 */
export const Badge = ({
  variant = 'default',
  size = 'sm',
  hasDot = false,
  children,
  className = '',
  ...props
}) => {
  return (
    <span
      className={`lifeos-badge lifeos-badge--${variant} lifeos-badge--${size} ${className}`}
      {...props}
    >
      {hasDot && <span className="lifeos-badge__dot" aria-hidden="true" />}
      <span className="lifeos-badge__text">{children}</span>
    </span>
  );
};

export default Badge;
