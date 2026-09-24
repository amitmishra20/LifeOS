import React, { forwardRef } from 'react';
import './Input.css';

/**
 * Accessible LifeOS Input Component
 */
export const Input = forwardRef(({
  id,
  label,
  error,
  hint,
  leftIcon = null,
  rightIcon = null,
  size = 'md', // 'sm' | 'md' | 'lg'
  className = '',
  disabled = false,
  ...props
}, ref) => {
  const inputId = id || (label ? `input-${label.toLowerCase().replace(/\s+/g, '-')}` : undefined);

  return (
    <div className={`lifeos-input-field lifeos-input-field--${size} ${error ? 'lifeos-input-field--error' : ''} ${className}`}>
      {label && (
        <label htmlFor={inputId} className="lifeos-input-label">
          {label}
        </label>
      )}

      <div className="lifeos-input-wrapper">
        {leftIcon && <span className="lifeos-input-icon lifeos-input-icon--left">{leftIcon}</span>}
        <input
          ref={ref}
          id={inputId}
          disabled={disabled}
          className={`lifeos-input ${leftIcon ? 'lifeos-input--has-left' : ''} ${
            rightIcon ? 'lifeos-input--has-right' : ''
          }`}
          aria-invalid={!!error}
          aria-describedby={error ? `${inputId}-error` : hint ? `${inputId}-hint` : undefined}
          {...props}
        />
        {rightIcon && <span className="lifeos-input-icon lifeos-input-icon--right">{rightIcon}</span>}
      </div>

      {error ? (
        <span id={`${inputId}-error`} className="lifeos-input-error-msg" role="alert">
          {error}
        </span>
      ) : hint ? (
        <span id={`${inputId}-hint`} className="lifeos-input-hint-msg">
          {hint}
        </span>
      ) : null}
    </div>
  );
});

Input.displayName = 'Input';

export default Input;
