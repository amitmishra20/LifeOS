import React from 'react';
import './Checkbox.css';

/**
 * Accessible LifeOS Checkbox with micro-satisfaction spring animation
 */
export const Checkbox = ({
  id,
  checked = false,
  onChange,
  label,
  description,
  disabled = false,
  className = '',
  ...props
}) => {
  const checkboxId = id || (label ? `check-${label.toLowerCase().replace(/\s+/g, '-')}` : undefined);

  return (
    <label
      htmlFor={checkboxId}
      className={`lifeos-checkbox-wrapper ${disabled ? 'lifeos-checkbox-wrapper--disabled' : ''} ${className}`}
    >
      <input
        type="checkbox"
        id={checkboxId}
        checked={checked}
        onChange={onChange}
        disabled={disabled}
        className="lifeos-checkbox-input"
        {...props}
      />
      <span className={`lifeos-checkbox-box ${checked ? 'lifeos-checkbox-box--checked' : ''}`}>
        <svg
          className="lifeos-checkbox-icon"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="3"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <polyline points="20 6 9 17 4 12" />
        </svg>
      </span>

      {(label || description) && (
        <span className="lifeos-checkbox-content">
          {label && <span className="lifeos-checkbox-label">{label}</span>}
          {description && <span className="lifeos-checkbox-desc">{description}</span>}
        </span>
      )}
    </label>
  );
};

export default Checkbox;
