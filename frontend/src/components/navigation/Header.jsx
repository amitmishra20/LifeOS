import React from 'react';
import { useLocation } from 'react-router-dom';
import { IconMenu, IconSearch, IconCommand } from '../ui/Icons';
import ThemeToggle from '../ui/ThemeToggle';
import './Header.css';

const ROUTE_TITLES = {
  '/': 'Home',
  '/focus': "Today's Focus",
  '/tasks': 'Tasks & Actions',
  '/habits': 'Habit Consistency',
  '/goals': 'Strategic Goals',
  '/calendar': 'Calendar & Time',
  '/learning': 'Continuous Learning',
  '/progress': 'Progress Metrics',
  '/analytics': 'System Analytics',
  '/goal-health': 'Goal Health Engine',
  '/recommendations': 'Focus Recommendations',
  '/notes': 'Capture & Notes',
  '/dev/foundation': 'Engine Foundation (Dev)',
};

export const Header = ({ onMenuClick, onQuickCaptureClick }) => {
  const location = useLocation();
  const currentTitle = ROUTE_TITLES[location.pathname] || 'Dashboard';
  const isMac = typeof window !== 'undefined' && /Mac|iPod|iPhone|iPad/.test(navigator.platform);

  return (
    <header className="lifeos-header">
      <div className="lifeos-header__left">
        {/* Mobile Menu Hamburger */}
        <button
          type="button"
          className="lifeos-header__mobile-menu-btn"
          onClick={onMenuClick}
          aria-label="Open mobile menu"
        >
          <IconMenu />
        </button>

        {/* Breadcrumb Hierarchy */}
        <div className="lifeos-header__breadcrumbs" aria-label="Breadcrumbs">
          <span className="lifeos-header__breadcrumb-root">LifeOS</span>
          <span className="lifeos-header__breadcrumb-divider" aria-hidden="true">
            /
          </span>
          <h1 className="lifeos-header__breadcrumb-current">{currentTitle}</h1>
        </div>
      </div>

      <div className="lifeos-header__center">
        {/* Global Quick Capture / Search Trigger */}
        <button
          type="button"
          className="lifeos-header__search-trigger"
          onClick={onQuickCaptureClick}
          aria-label="Open quick capture or search"
        >
          <IconSearch width="14" height="14" />
          <span className="lifeos-header__search-text">Quick Capture / Search...</span>
          <kbd className="lifeos-header__search-kbd">
            {isMac ? <IconCommand width="10" height="10" /> : 'Ctrl'} K
          </kbd>
        </button>
      </div>

      <div className="lifeos-header__right">
        {/* Theme Toggle (Deep Black / Light) */}
        <ThemeToggle />

        {/* Notification Bell with Active Indicator */}
        <button
          type="button"
          className="lifeos-header__bell-btn"
          aria-label="Notifications"
          title="Notifications"
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
            <path d="M13.73 21a2 2 0 0 1-3.46 0" />
          </svg>
          <span className="lifeos-header__bell-dot" />
        </button>

        {/* Honest Static Local Identity Element (Phase 1-3 Local Preview) */}
        <div className="lifeos-header__user-pill lifeos-header__user-pill--static" title="Local session (Development preview)">
          <div className="lifeos-header__avatar" aria-hidden="true">A</div>
          <span className="lifeos-header__username">Amit</span>
          <span className="lifeos-header__session-tag">Local</span>
        </div>
      </div>
    </header>
  );
};

export default Header;
