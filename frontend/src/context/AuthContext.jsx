import React, { useState, useEffect, useCallback } from 'react';
import * as authService from '../services/authService';
import { AUTH_STATUS } from './authConstants';
import { AuthContext } from './authContextDef';

export const AuthProvider = ({ children }) => {
  const [status, setStatus] = useState(AUTH_STATUS.LOADING);
  const [user, setUser] = useState(null);
  const [error, setError] = useState(null);

  // Check current session on application startup
  const checkSession = useCallback(async () => {
    try {
      const currentUser = await authService.getCurrentUser();
      setUser(currentUser);
      setStatus(AUTH_STATUS.AUTHENTICATED);
      setError(null);
      return currentUser;
    } catch {
      setUser(null);
      setStatus(AUTH_STATUS.UNAUTHENTICATED);
      return null;
    }
  }, []);

  useEffect(() => {
    let isMounted = true;

    authService.getCurrentUser()
      .then((currentUser) => {
        if (isMounted) {
          setUser(currentUser);
          setStatus(AUTH_STATUS.AUTHENTICATED);
          setError(null);
        }
      })
      .catch(() => {
        if (isMounted) {
          setUser(null);
          setStatus(AUTH_STATUS.UNAUTHENTICATED);
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

  const login = async (credentials) => {
    setError(null);
    try {
      const loggedInUser = await authService.login(credentials);
      setUser(loggedInUser);
      setStatus(AUTH_STATUS.AUTHENTICATED);
      return loggedInUser;
    } catch (err) {
      setError(err.message || 'Login failed');
      throw err;
    }
  };

  const register = async (data) => {
    setError(null);
    try {
      const registeredUser = await authService.register(data);
      setUser(registeredUser);
      setStatus(AUTH_STATUS.AUTHENTICATED);
      return registeredUser;
    } catch (err) {
      setError(err.message || 'Registration failed');
      throw err;
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
    } catch (err) {
      console.warn('Logout network request completed with warning:', err.message);
    } finally {
      setUser(null);
      setStatus(AUTH_STATUS.UNAUTHENTICATED);
      setError(null);
    }
  };

  const clearError = () => setError(null);

  const value = {
    status,
    user,
    error,
    isLoading: status === AUTH_STATUS.LOADING,
    isAuthenticated: status === AUTH_STATUS.AUTHENTICATED,
    login,
    register,
    logout,
    checkSession,
    clearError,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export default AuthContext;
