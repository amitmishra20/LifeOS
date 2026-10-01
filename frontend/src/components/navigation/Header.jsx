import React, { useState, useRef, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/useAuth';
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
  '/settings': 'Settings & Account',
};

export const Header = ({ onMenuClick, onQuickCaptureClick }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const [dropdownOpen, setDropdownOpen] = useState(false);
  const menuRef = useRef(null);

  const currentTitle = ROUTE_TITLES[location.pathname] || 'Dashboard';
  const isMac = typeof window !== 'undefined' && /Mac|iPod|iPhone|iPad/.test(navigator.platform);

  // Close dropdown on outside click or escape
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setDropdownOpen(false);
      }
    };

    const handleKeyDown = (event) => {
      if (event.key === 'Escape') {
        setDropdownOpen(false);
      }
    };

    if (dropdownOpen) {
      document.addEventListener('mousedown', handleClickOutside);
      document.addEventListener('keydown', handleKeyDown);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [dropdownOpen]);

  const initials = user?.name
    ? user.name
        .split(' ')
        .map((p) => p[0])
        .join('')
        .toUpperCase()
        .slice(0, 2)
    : 'U';

  const handleLogout = async () => {
    setDropdownOpen(false);
    await logout();
    navigate('/login', { replace: true });
  };

  return (
    <>
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
            aria-label="Open LifeOS search and quick capture"
          >
            <IconSearch width="14" height="14" />
            <span className="lifeos-header__search-text">Search your life...</span>
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

          {/* Authenticated User Account Menu */}
          <div className="lifeos-header__user-container" ref={menuRef}>
            <button
              type="button"
              className="lifeos-header__user-pill lifeos-header__user-pill--interactive"
              onClick={() => setDropdownOpen((prev) => !prev)}
              aria-expanded={dropdownOpen}
              aria-haspopup="menu"
              aria-label={`User account menu for ${user?.name || 'User'}`}
            >
              <div className="lifeos-header__avatar" aria-hidden="true">
                {initials}
              </div>
              <span className="lifeos-header__username">{user?.name || 'Account'}</span>
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
                <path d="M6 9l6 6 6-6" />
              </svg>
            </button>

            {dropdownOpen && (
              <div className="lifeos-header__dropdown" role="menu">
                <div className="lifeos-header__dropdown-userinfo">
                  <div className="lifeos-header__dropdown-name">{user?.name || 'User'}</div>
                  <div className="lifeos-header__dropdown-email">{user?.email || ''}</div>
                </div>

                <button
                  type="button"
                  className="lifeos-header__dropdown-item"
                  role="menuitem"
                  onClick={() => {
                    setDropdownOpen(false);
                    navigate('/settings');
                  }}
                >
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="12" cy="12" r="3" />
                    <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z" />
                  </svg>
                  <span>Settings & Account</span>
                </button>

                <button
                  type="button"
                  className="lifeos-header__dropdown-item lifeos-header__dropdown-item--danger"
                  role="menuitem"
                  onClick={handleLogout}
                >
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                    <polyline points="16 17 21 12 16 7" />
                    <line x1="21" y1="12" x2="9" y2="12" />
                  </svg>
                  <span>Logout</span>
                </button>
              </div>
            )}
          </div>
        </div>
      </header>
    </>
  );
};

export default Header;
