import React from 'react';
import './Card.css';

/**
 * Reusable Surface Container adhering to LifeOS multi-tiered depth
 * @param {'card' | 'elevated' | 'subtle' | 'ghost' | 'flat'} variant
 * @param {boolean} interactive
 * @param {boolean} padded
 * @param {React.ReactNode} children
 */
export const Card = ({
  variant = 'card',
  interactive = false,
  padded = true,
  children,
  className = '',
  ...props
}) => {
  return (
    <div
      className={`lifeos-surface lifeos-surface--${variant} ${
        interactive ? 'lifeos-surface--interactive' : ''
      } ${padded ? 'lifeos-surface--padded' : ''} ${className}`}
      {...props}
    >
      {children}
    </div>
  );
};

export const CardHeader = ({ children, className = '', ...props }) => (
  <div className={`lifeos-surface__header ${className}`} {...props}>
    {children}
  </div>
);

export const CardTitle = ({ as: Component = 'h3', children, className = '', ...props }) => (
  <Component className={`lifeos-surface__title ${className}`} {...props}>
    {children}
  </Component>
);

export const CardDescription = ({ children, className = '', ...props }) => (
  <p className={`lifeos-surface__desc ${className}`} {...props}>
    {children}
  </p>
);

export const CardBody = ({ children, className = '', ...props }) => (
  <div className={`lifeos-surface__body ${className}`} {...props}>
    {children}
  </div>
);

export const CardFooter = ({ children, className = '', ...props }) => (
  <div className={`lifeos-surface__footer ${className}`} {...props}>
    {children}
  </div>
);

export default Card;
