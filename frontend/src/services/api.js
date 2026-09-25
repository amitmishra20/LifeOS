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
    // If backend returns ErrorResponse, normalize it
    if (error.response && error.response.data) {
      const data = error.response.data;
      const message = data.message || (typeof data === 'string' ? data : 'An unexpected error occurred');
      const normalizedError = new Error(message);
      normalizedError.status = data.status || error.response.status;
      normalizedError.code = data.code || data.error;
      normalizedError.path = data.path;
      normalizedError.timestamp = data.timestamp;
      normalizedError.fieldErrors = data.fieldErrors || {};
      normalizedError.validationErrors = data.errors || [];
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
