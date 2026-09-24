import React from 'react';
import './Tabs.css';

/**
 * Accessible LifeOS Tabs Component
 */
export const Tabs = ({
  tabs = [],
  activeTab,
  onChange,
  variant = 'pills', // 'pills' | 'underline'
  size = 'md',       // 'sm' | 'md'
  className = '',
}) => {
  const handleKeyDown = (e, index) => {
    if (e.key === 'ArrowRight') {
      const nextIndex = (index + 1) % tabs.length;
      onChange(tabs[nextIndex].id);
    } else if (e.key === 'ArrowLeft') {
      const prevIndex = (index - 1 + tabs.length) % tabs.length;
      onChange(tabs[prevIndex].id);
    }
  };

  return (
    <div
      role="tablist"
      className={`lifeos-tabs lifeos-tabs--${variant} lifeos-tabs--${size} ${className}`}
    >
      {tabs.map((tab, idx) => {
        const isActive = activeTab === tab.id;
        return (
          <button
            key={tab.id}
            role="tab"
            type="button"
            id={`tab-${tab.id}`}
            aria-selected={isActive}
            aria-controls={`panel-${tab.id}`}
            tabIndex={isActive ? 0 : -1}
            onClick={() => onChange(tab.id)}
            onKeyDown={(e) => handleKeyDown(e, idx)}
            className={`lifeos-tab ${isActive ? 'lifeos-tab--active' : ''}`}
          >
            {tab.icon && <span className="lifeos-tab__icon">{tab.icon}</span>}
            <span className="lifeos-tab__label">{tab.label}</span>
            {tab.badge !== undefined && (
              <span className="lifeos-tab__badge">{tab.badge}</span>
            )}
          </button>
        );
      })}
    </div>
  );
};

export default Tabs;
