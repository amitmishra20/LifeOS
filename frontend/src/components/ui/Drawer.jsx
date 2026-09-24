import React, { useEffect } from 'react';
import './Drawer.css';

/**
 * Accessible LifeOS Drawer / Slide-Over Sheet Component
 */
export const Drawer = ({
  isOpen = false,
  onClose,
  title,
  position = 'right', // 'right' | 'bottom'
  size = 'md',        // 'sm' | 'md' | 'lg'
  children,
  className = '',
}) => {
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };

    if (isOpen) {
      document.body.style.overflow = 'hidden';
      window.addEventListener('keydown', handleKeyDown);
    } else {
      document.body.style.overflow = '';
    }

    return () => {
      document.body.style.overflow = '';
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <div className="lifeos-drawer-overlay" onClick={onClose} role="presentation">
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby={title ? 'drawer-title' : undefined}
        className={`lifeos-drawer lifeos-drawer--${position} lifeos-drawer--${size} ${className}`}
        onClick={(e) => e.stopPropagation()}
      >
        {position === 'bottom' && (
          <div className="lifeos-drawer__handle-bar" aria-hidden="true">
            <span className="lifeos-drawer__handle" />
          </div>
        )}

        <div className="lifeos-drawer__header">
          {title && (
            <h2 id="drawer-title" className="lifeos-drawer__title">
              {title}
            </h2>
          )}
          <button
            type="button"
            className="lifeos-drawer__close-btn"
            onClick={onClose}
            aria-label="Close drawer"
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="18" y1="6" x2="6" y2="18" />
              <line x1="6" y1="6" x2="18" y2="18" />
            </svg>
          </button>
        </div>

        <div className="lifeos-drawer__body">{children}</div>
      </div>
    </div>
  );
};

export default Drawer;
