import React, { forwardRef } from 'react';
import './Textarea.css';

/**
 * Accessible LifeOS Textarea Component
 */
export const Textarea = forwardRef(({
  id,
  label,
  error,
  hint,
  rows = 3,
  className = '',
  disabled = false,
  ...props
}, ref) => {
  const textareaId = id || (label ? `textarea-${label.toLowerCase().replace(/\s+/g, '-')}` : undefined);

  return (
    <div className={`lifeos-textarea-field ${error ? 'lifeos-textarea-field--error' : ''} ${className}`}>
      {label && (
        <label htmlFor={textareaId} className="lifeos-textarea-label">
          {label}
        </label>
      )}

      <textarea
        ref={ref}
        id={textareaId}
        rows={rows}
        disabled={disabled}
        className="lifeos-textarea"
        aria-invalid={!!error}
        aria-describedby={error ? `${textareaId}-error` : hint ? `${textareaId}-hint` : undefined}
        {...props}
      />

      {error ? (
        <span id={`${textareaId}-error`} className="lifeos-textarea-error-msg" role="alert">
          {error}
        </span>
      ) : hint ? (
        <span id={`${textareaId}-hint`} className="lifeos-textarea-hint-msg">
          {hint}
        </span>
      ) : null}
    </div>
  );
});

Textarea.displayName = 'Textarea';

export default Textarea;
