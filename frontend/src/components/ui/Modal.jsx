import React, { useEffect, useRef } from 'react';
import './Modal.css';

/**
 * Accessible LifeOS Modal Component
 * Supports ESC to close, backdrop click, body scroll locking, and smooth micro-entrance
 */
export const Modal = ({
  isOpen = false,
  onClose,
  title,
  description,
  size = 'md', // 'sm' | 'md' | 'lg' | 'xl'
  children,
  className = '',
}) => {
  const modalRef = useRef(null);

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
    <div className="lifeos-modal-overlay" onClick={onClose} role="presentation">
      <div
        ref={modalRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby={title ? 'modal-title' : undefined}
        aria-describedby={description ? 'modal-desc' : undefined}
        className={`lifeos-modal lifeos-modal--${size} ${className}`}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="lifeos-modal__header">
          <div>
            {title && (
              <h2 id="modal-title" className="lifeos-modal__title">
                {title}
              </h2>
            )}
            {description && (
              <p id="modal-desc" className="lifeos-modal__desc">
                {description}
              </p>
            )}
          </div>
          <button
            type="button"
            className="lifeos-modal__close-btn"
            onClick={onClose}
            aria-label="Close modal"
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="18" y1="6" x2="6" y2="18" />
              <line x1="6" y1="6" x2="18" y2="18" />
            </svg>
          </button>
        </div>

        <div className="lifeos-modal__body">{children}</div>
      </div>
    </div>
  );
};

export default Modal;
