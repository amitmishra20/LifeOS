import React, { useState, useEffect } from 'react';
import { Outlet, NavLink } from 'react-router-dom';
import Sidebar from '../components/navigation/Sidebar';
import { NAV_GROUPS } from '../components/navigation/navConfig';
import Header from '../components/navigation/Header';
import MobileNav from '../components/navigation/MobileNav';
import Drawer from '../components/ui/Drawer';
import QuickCaptureModal from '../components/ui/QuickCaptureModal';
import './AppLayout.css';

export const AppLayout = () => {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [mobileDrawerOpen, setMobileDrawerOpen] = useState(false);
  const [quickCaptureOpen, setQuickCaptureOpen] = useState(false);

  // Global Keyboard Shortcuts
  useEffect(() => {
    const handleKeyDown = (e) => {
      // Cmd/Ctrl + K opens Quick Capture
      if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        setQuickCaptureOpen((prev) => !prev);
        return;
      }

      // 'c' opens Quick Capture when not focused on an input element
      const activeTag = document.activeElement ? document.activeElement.tagName.toLowerCase() : '';
      if (
        e.key.toLowerCase() === 'c' &&
        !e.metaKey &&
        !e.ctrlKey &&
        !e.altKey &&
        activeTag !== 'input' &&
        activeTag !== 'textarea' &&
        activeTag !== 'select'
      ) {
        e.preventDefault();
        setQuickCaptureOpen(true);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  const handleCapture = (item) => {
    // Phase 3 signature prototype interaction: emits event or logs capture
    console.info('[LifeOS Signature Interaction] Quick Capture item created:', item);
    window.dispatchEvent(new CustomEvent('lifeos:captured-item', { detail: item }));
  };

  return (
    <div className={`lifeos-app ${sidebarCollapsed ? 'lifeos-app--collapsed' : ''}`}>
      {/* Desktop Sidebar */}
      <Sidebar
        isCollapsed={sidebarCollapsed}
        onToggleCollapse={() => setSidebarCollapsed((prev) => !prev)}
      />

      {/* Main View Area */}
      <div className="lifeos-main">
        <Header
          onMenuClick={() => setMobileDrawerOpen(true)}
          onQuickCaptureClick={() => setQuickCaptureOpen(true)}
        />

        <main className="lifeos-content" id="main-content">
          <Outlet />
        </main>
      </div>

      {/* Mobile Fixed Bottom Navigation */}
      <MobileNav
        onOpenMenu={() => setMobileDrawerOpen(true)}
        onQuickCapture={() => setQuickCaptureOpen(true)}
      />

      {/* Mobile Full Navigation Sheet */}
      <Drawer
        isOpen={mobileDrawerOpen}
        onClose={() => setMobileDrawerOpen(false)}
        title="LifeOS Navigation"
        position="bottom"
      >
        <div className="lifeos-mobile-drawer">
          {NAV_GROUPS.map((group) => (
            <div key={group.title} className="lifeos-mobile-drawer__group">
              <div className="lifeos-mobile-drawer__group-title">{group.title}</div>
              <div className="lifeos-mobile-drawer__group-items">
                {group.items.map((item) => (
                  <NavLink
                    key={item.path}
                    to={item.path}
                    end={item.path === '/'}
                    onClick={() => setMobileDrawerOpen(false)}
                    className={({ isActive }) =>
                      `lifeos-mobile-drawer__link ${isActive ? 'lifeos-mobile-drawer__link--active' : ''}`
                    }
                  >
                    <span className="lifeos-mobile-drawer__icon">{item.icon}</span>
                    <span className="lifeos-mobile-drawer__label">{item.label}</span>
                  </NavLink>
                ))}
              </div>
            </div>
          ))}

          <div className="lifeos-mobile-drawer__dev">
            <NavLink
              to="/dev/foundation"
              onClick={() => setMobileDrawerOpen(false)}
              className="lifeos-mobile-drawer__dev-link"
            >
              Engine Foundation (Phase 1/2 Diagnostics)
            </NavLink>
          </div>
        </div>
      </Drawer>

      {/* Signature Interaction Prototype: Global Quick Capture Modal */}
      <QuickCaptureModal
        isOpen={quickCaptureOpen}
        onClose={() => setQuickCaptureOpen(false)}
        onCapture={handleCapture}
      />
    </div>
  );
};

export default AppLayout;
