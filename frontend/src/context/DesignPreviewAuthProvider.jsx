import React, { useMemo, useState } from 'react';
import { AuthContext } from './authContextDef';
import { AUTH_STATUS } from './authConstants';

const PREVIEW_USER = {
  id: 'design-preview-user',
  name: 'Design Preview',
  email: 'design-preview@lifeos.invalid',
};

/**
 * Development-only auth context for visually inspecting protected screens.
 * This is selected by App.jsx only when Vite is running in design preview mode.
 */
export const DesignPreviewAuthProvider = ({ children }) => {
  const [user] = useState(PREVIEW_USER);
  const value = useMemo(() => ({
    status: AUTH_STATUS.AUTHENTICATED,
    user,
    error: null,
    isLoading: false,
    isAuthenticated: true,
    login: async () => user,
    register: async () => user,
    logout: async () => undefined,
    checkSession: async () => user,
    clearError: () => undefined,
  }), [user]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export default DesignPreviewAuthProvider;
