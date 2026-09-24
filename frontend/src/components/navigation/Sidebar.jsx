import React from 'react';
import { NavLink } from 'react-router-dom';
import Tooltip from '../ui/Tooltip';
import {
  IconDashboard,
  IconTodayFocus,
  IconTasks,
  IconHabits,
  IconGoals,
  IconCalendar,
  IconLearning,
  IconProgress,
  IconAnalytics,
  IconGoalHealth,
  IconRecommendations,
  IconNotes,
  IconCollapse,
  IconExpand,
} from '../ui/Icons';
import './Sidebar.css';

import { NAV_GROUPS } from './navConfig';

export const Sidebar = ({
  isCollapsed = false,
  onToggleCollapse,
  isMobileOpen = false,
  onCloseMobile,
}) => {
  return (
    <aside
      className={`lifeos-sidebar ${isCollapsed ? 'lifeos-sidebar--collapsed' : ''} ${
        isMobileOpen ? 'lifeos-sidebar--mobile-open' : ''
      }`}
      aria-label="Main Navigation"
    >
      {/* Brand Header */}
      <div className="lifeos-sidebar__brand">
        <NavLink to="/" className="lifeos-sidebar__logo-link">
          <div className="lifeos-sidebar__logo-mark" aria-hidden="true">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lifeos-sidebar__leaf-svg">
              <path d="M11 20A7 7 0 0 1 4 13C4 7 9 3 17 3c1 5-1 11-6 17z" />
              <path d="M7 17l8-8" />
            </svg>
          </div>
          {!isCollapsed && (
            <div className="lifeos-sidebar__brand-text">
              <span className="lifeos-sidebar__brand-name">LifeOS</span>
            </div>
          )}
        </NavLink>
      </div>

      {/* Navigation List */}
      <nav className="lifeos-sidebar__nav">
        {NAV_GROUPS.map((group, idx) => (
          <div key={group.title || `group-${idx}`} className="lifeos-sidebar__group">
            {!isCollapsed && group.title && (
              <div className="lifeos-sidebar__group-title">{group.title}</div>
            )}
            <ul className="lifeos-sidebar__group-list">
              {group.items.map((item) => {
                const navLinkContent = (
                  <NavLink
                    to={item.path}
                    end={item.path === '/'}
                    onClick={() => {
                      if (onCloseMobile) onCloseMobile();
                    }}
                    className={({ isActive }) =>
                      `lifeos-sidebar__link ${isActive ? 'lifeos-sidebar__link--active' : ''}`
                    }
                  >
                    <span className="lifeos-sidebar__link-icon">{item.icon}</span>
                    {!isCollapsed && (
                      <span className="lifeos-sidebar__link-label">{item.label}</span>
                    )}
                  </NavLink>
                );

                return (
                  <li key={item.label} className="lifeos-sidebar__item">
                    {isCollapsed ? (
                      <Tooltip content={item.label} position="right">
                        {navLinkContent}
                      </Tooltip>
                    ) : (
                      navLinkContent
                    )}
                  </li>
                );
              })}
            </ul>
          </div>
        ))}
      </nav>

      {/* Footer Controls & System Status */}
      <div className="lifeos-sidebar__footer">
        <NavLink
          to="/dev/foundation"
          className="lifeos-sidebar__status-link"
          title="Phase 1 Engine & Diagnostics"
        >
          <span className="lifeos-sidebar__status-dot" />
          {!isCollapsed && (
            <span className="lifeos-sidebar__status-text">Engine Foundation</span>
          )}
        </NavLink>

        {/* Desktop Collapse Toggle */}
        <button
          type="button"
          className="lifeos-sidebar__collapse-btn"
          onClick={onToggleCollapse}
          aria-label={isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}
        >
          {isCollapsed ? <IconExpand /> : <IconCollapse />}
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
