import api from './api';

/**
 * Authentication Service for LifeOS
 * Uses secure HttpOnly cookies (withCredentials: true in api.js).
 * Tokens are never stored in localStorage or sessionStorage.
 */

export const register = async ({ name, email, password }) => {
  const response = await api.post('/auth/register', { name, email, password });
  return response.data;
};

export const login = async ({ email, password }) => {
  const response = await api.post('/auth/login', { email, password });
  return response.data;
};

export const logout = async () => {
  const response = await api.post('/auth/logout');
  return response.data;
};

export const getCurrentUser = async () => {
  const response = await api.get('/auth/me');
  return response.data;
};

export const getCsrfToken = async () => {
  const response = await api.get('/auth/csrf');
  return response.data;
};

export default {
  register,
  login,
  logout,
  getCurrentUser,
  getCsrfToken,
};
