import React from 'react';
import './Button.css';

/**
 * Reusable LifeOS Button Component
 * Supports micro-physics, icon slots, loading states, and accessible keyboard focus.
 */
export const Button = ({
  variant = 'primary', // 'primary' | 'secondary' | 'subtle' | 'outline' | 'ghost' | 'danger'
  size = 'md',        // 'xs' | 'sm' | 'md' | 'lg'
  isLoading = false,
  disabled = false,
  leftIcon = null,
  rightIcon = null,
  iconOnly = false,
  children,
  className = '',
  type = 'button',
  ...props
}) => {
  return (
    <button
      type={type}
      className={`lifeos-btn lifeos-btn--${variant} lifeos-btn--${size} ${
        isLoading ? 'lifeos-btn--loading' : ''
      } ${iconOnly ? 'lifeos-btn--icon-only' : ''} ${className}`}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading ? (
        <span className="lifeos-btn__spinner" aria-hidden="true" />
      ) : (
        leftIcon && <span className="lifeos-btn__icon lifeos-btn__icon--left">{leftIcon}</span>
      )}
      {!iconOnly && children && <span className="lifeos-btn__label">{children}</span>}
      {!isLoading && rightIcon && (
        <span className="lifeos-btn__icon lifeos-btn__icon--right">{rightIcon}</span>
      )}
    </button>
  );
};

export default Button;
