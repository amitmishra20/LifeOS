import axios from 'axios';

/**
 * LifeOS Axios Client
 * Configured with /api/v1 namespace and withCredentials for HttpOnly cookie session support
 */
const api = axios.create({
  baseURL: '/api/v1',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
});

// Helper to extract cookie by name
function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
  return null;
}

// Request interceptor: attach CSRF token if cookie is present
api.interceptors.request.use(
  (config) => {
    const xsrfToken = getCookie('XSRF-TOKEN');
    if (xsrfToken) {
      config.headers['X-XSRF-TOKEN'] = xsrfToken;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: handle standardized error formatting
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // If backend returns RFC-7807 ErrorResponse, normalize it
    if (error.response && error.response.data) {
      const { status, error: errName, message, path, timestamp, errors } = error.response.data;
      const normalizedError = new Error(message || 'An unexpected error occurred');
      normalizedError.status = status || error.response.status;
      normalizedError.error = errName;
      normalizedError.path = path;
      normalizedError.timestamp = timestamp;
      normalizedError.validationErrors = errors || [];
      return Promise.reject(normalizedError);
    }
    return Promise.reject(error);
  }
);

/**
 * Phase 1 Health Verification API
 */
export const checkHealth = async () => {
  const response = await api.get('/health');
  return response.data;
};

export default api;
