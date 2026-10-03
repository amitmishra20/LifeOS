/**
 * LifeOS Design Preview Mode Helper
 * 
 * STRICTLY DEVELOPMENT-ONLY:
 * Enables V0 and frontend developers to preview, render, and navigate
 * the complete LifeOS frontend without requiring the Spring Boot / MySQL backend.
 * 
 * This module is dead-code eliminated in production builds.
 */

export const isDesignPreviewActive = () => {
  if (!import.meta.env.DEV) {
    return false;
  }

  // Check URL search parameters
  if (typeof window !== 'undefined' && window.location) {
    const params = new URLSearchParams(window.location.search);
    if (params.get('designPreview') === 'true' || params.get('preview') === 'true') {
      try {
        localStorage.setItem('lifeos_design_preview', 'true');
      } catch {
        // Storage might be blocked in some sandboxes
      }
      return true;
    }
    if (params.get('designPreview') === 'false' || params.get('preview') === 'false') {
      try {
        localStorage.removeItem('lifeos_design_preview');
      } catch {
        // Storage might be blocked
      }
      return false;
    }
    
    // Check localStorage persistence
    try {
      if (localStorage.getItem('lifeos_design_preview') === 'true') {
        return true;
      }
    } catch {
      // Storage access error fallback
    }
  }

  // Check Vite environment variable
  if (import.meta.env.VITE_DESIGN_PREVIEW === 'true') {
    return true;
  }

  return false;
};

export const enableDesignPreview = () => {
  if (import.meta.env.DEV && typeof window !== 'undefined') {
    try {
      localStorage.setItem('lifeos_design_preview', 'true');
    } catch {}
    window.location.reload();
  }
};

export const disableDesignPreview = () => {
  if (typeof window !== 'undefined') {
    try {
      localStorage.removeItem('lifeos_design_preview');
    } catch {}
    window.location.reload();
  }
};
