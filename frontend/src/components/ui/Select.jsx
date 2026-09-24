import React, { forwardRef } from 'react';
import './Select.css';

/**
 * Accessible LifeOS Custom Select Component
 */
export const Select = forwardRef(({
  id,
  label,
  error,
  hint,
  options = [],
  className = '',
  disabled = false,
  children,
  ...props
}, ref) => {
  const selectId = id || (label ? `select-${label.toLowerCase().replace(/\s+/g, '-')}` : undefined);

  return (
    <div className={`lifeos-select-field ${error ? 'lifeos-select-field--error' : ''} ${className}`}>
      {label && (
        <label htmlFor={selectId} className="lifeos-select-label">
          {label}
        </label>
      )}

      <div className="lifeos-select-wrapper">
        <select
          ref={ref}
          id={selectId}
          disabled={disabled}
          className="lifeos-select"
          aria-invalid={!!error}
          aria-describedby={error ? `${selectId}-error` : hint ? `${selectId}-hint` : undefined}
          {...props}
        >
          {children || options.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
        <span className="lifeos-select-arrow" aria-hidden="true">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <polyline points="6 9 12 15 18 9" />
          </svg>
        </span>
      </div>

      {error ? (
        <span id={`${selectId}-error`} className="lifeos-select-error-msg" role="alert">
          {error}
        </span>
      ) : hint ? (
        <span id={`${selectId}-hint`} className="lifeos-select-hint-msg">
          {hint}
        </span>
      ) : null}
    </div>
  );
});

Select.displayName = 'Select';

export default Select;
