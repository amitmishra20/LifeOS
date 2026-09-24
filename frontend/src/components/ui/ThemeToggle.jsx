import React, { useState, useEffect } from 'react';
import { IconSun, IconMoon } from './Icons';
import './ThemeToggle.css';

export const ThemeToggle = ({ className = '' }) => {
  const [theme, setTheme] = useState('dark');

  useEffect(() => {
    // Initial theme detection from html data-theme or localStorage
    const savedTheme = localStorage.getItem('lifeos_theme');
    const currentTheme = savedTheme || document.documentElement.getAttribute('data-theme') || 'dark';
    setTheme(currentTheme);
    document.documentElement.setAttribute('data-theme', currentTheme);
  }, []);

  const toggleTheme = () => {
    const nextTheme = theme === 'dark' ? 'light' : 'dark';
    setTheme(nextTheme);
    document.documentElement.setAttribute('data-theme', nextTheme);
    localStorage.setItem('lifeos_theme', nextTheme);

    // Update meta theme-color for mobile header
    const metaThemeColor = document.querySelector('meta[name="theme-color"]');
    if (metaThemeColor) {
      metaThemeColor.setAttribute('content', nextTheme === 'dark' ? '#050507' : '#F5F5F3');
    }
  };

  return (
    <button
      type="button"
      className={`lifeos-theme-toggle ${className}`}
      onClick={toggleTheme}
      aria-label={`Switch to ${theme === 'dark' ? 'light' : 'dark'} theme`}
      title={`Switch to ${theme === 'dark' ? 'light' : 'dark'} theme`}
    >
      <span className="lifeos-theme-toggle__icon">
        {theme === 'dark' ? <IconSun width="16" height="16" /> : <IconMoon width="16" height="16" />}
      </span>
      <span className="lifeos-theme-toggle__label">
        {theme === 'dark' ? 'Light Mode' : 'Deep Black'}
      </span>
    </button>
  );
};

export default ThemeToggle;
