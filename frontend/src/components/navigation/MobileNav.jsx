import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  IconDashboard,
  IconTodayFocus,
  IconPlus,
  IconGoals,
  IconMenu,
} from '../ui/Icons';
import './MobileNav.css';

/**
 * Mobile Bottom Navigation adhering to design.md priority:
 * "Capture -> Focus -> Execute"
 */
export const MobileNav = ({ onOpenMenu, onQuickCapture }) => {
  return (
    <nav className="lifeos-mobile-nav" aria-label="Mobile Bottom Navigation">
      <div className="lifeos-mobile-nav__bar">
        {/* 1. Home */}
        <NavLink
          to="/"
          end
          className={({ isActive }) =>
            `lifeos-mobile-nav__item ${isActive ? 'lifeos-mobile-nav__item--active' : ''}`
          }
        >
          <IconDashboard width="20" height="20" />
          <span className="lifeos-mobile-nav__label">Home</span>
        </NavLink>

        {/* 2. Focus */}
        <NavLink
          to="/focus"
          className={({ isActive }) =>
            `lifeos-mobile-nav__item ${isActive ? 'lifeos-mobile-nav__item--active' : ''}`
          }
        >
          <IconTodayFocus width="20" height="20" />
          <span className="lifeos-mobile-nav__label">Focus</span>
        </NavLink>

        {/* 3. Quick Capture (Center Prominent Trigger) */}
        <button
          type="button"
          className="lifeos-mobile-nav__capture-btn"
          onClick={onQuickCapture}
          aria-label="Quick Capture new task or note"
        >
          <div className="lifeos-mobile-nav__capture-circle">
            <IconPlus width="22" height="22" />
          </div>
          <span className="lifeos-mobile-nav__label">Capture</span>
        </button>

        {/* 4. Strategic Goals */}
        <NavLink
          to="/goals"
          className={({ isActive }) =>
            `lifeos-mobile-nav__item ${isActive ? 'lifeos-mobile-nav__item--active' : ''}`
          }
        >
          <IconGoals width="20" height="20" />
          <span className="lifeos-mobile-nav__label">Goals</span>
        </NavLink>

        {/* 5. Menu (Opens Drawer) */}
        <button
          type="button"
          className="lifeos-mobile-nav__item"
          onClick={onOpenMenu}
          aria-label="Open full LifeOS navigation menu"
        >
          <IconMenu width="20" height="20" />
          <span className="lifeos-mobile-nav__label">Menu</span>
        </button>
      </div>
    </nav>
  );
};

export default MobileNav;
