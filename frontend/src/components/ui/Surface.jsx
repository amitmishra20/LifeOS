import React from 'react';
import './Surface.css';

export const Surface = ({
  children,
  variant = 'open', // 'open' | 'subtle' | 'raised' | 'card'
  as: Component = 'div',
  className = '',
  ...props
}) => {
  return (
    <Component
      className={`lifeos-surface lifeos-surface--${variant} ${className}`}
      {...props}
    >
      {children}
    </Component>
  );
};

export default Surface;
