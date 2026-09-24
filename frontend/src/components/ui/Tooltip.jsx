import React, { useState } from 'react';
import './Tooltip.css';

/**
 * Accessible LifeOS Tooltip Component
 */
export const Tooltip = ({
  content,
  position = 'top', // 'top' | 'bottom' | 'left' | 'right'
  children,
  className = '',
}) => {
  const [isVisible, setIsVisible] = useState(false);

  if (!content) return children;

  return (
    <div
      className={`lifeos-tooltip-anchor ${className}`}
      onMouseEnter={() => setIsVisible(true)}
      onMouseLeave={() => setIsVisible(false)}
      onFocus={() => setIsVisible(true)}
      onBlur={() => setIsVisible(false)}
      onKeyDown={(e) => {
        if (e.key === 'Escape') setIsVisible(false);
      }}
    >
      {children}
      {isVisible && (
        <div
          role="tooltip"
          className={`lifeos-tooltip lifeos-tooltip--${position}`}
        >
          {content}
        </div>
      )}
    </div>
  );
};

export default Tooltip;
