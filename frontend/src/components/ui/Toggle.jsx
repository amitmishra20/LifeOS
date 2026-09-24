import React from 'react';
import './Toggle.css';

/**
 * Accessible LifeOS Toggle Component
 */
export const Toggle = ({
  id,
  checked = false,
  onChange,
  label,
  description,
  disabled = false,
  size = 'md', // 'sm' | 'md'
  className = '',
  ...props
}) => {
  const toggleId = id || (label ? `toggle-${label.toLowerCase().replace(/\s+/g, '-')}` : undefined);

  return (
    <label
      htmlFor={toggleId}
      className={`lifeos-toggle-wrapper lifeos-toggle-wrapper--${size} ${
        disabled ? 'lifeos-toggle-wrapper--disabled' : ''
      } ${className}`}
    >
      <input
        type="checkbox"
        id={toggleId}
        role="switch"
        aria-checked={checked}
        checked={checked}
        onChange={onChange}
        disabled={disabled}
        className="lifeos-toggle-input"
        {...props}
      />
      <span className={`lifeos-toggle-track ${checked ? 'lifeos-toggle-track--checked' : ''}`}>
        <span className="lifeos-toggle-thumb" />
      </span>

      {(label || description) && (
        <span className="lifeos-toggle-content">
          {label && <span className="lifeos-toggle-label">{label}</span>}
          {description && <span className="lifeos-toggle-desc">{description}</span>}
        </span>
      )}
    </label>
  );
};

export default Toggle;
