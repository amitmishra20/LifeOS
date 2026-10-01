import api from './api';

export const analyticsService = {
  async getDashboard() {
    const response = await api.get('/analytics/dashboard');
    return response.data;
  },

  async getProductivity() {
    const response = await api.get('/analytics/productivity');
    return response.data;
  },

  async getGoals() {
    const response = await api.get('/analytics/goals');
    return response.data;
  },

  async getHabits() {
    const response = await api.get('/analytics/habits');
    return response.data;
  },
};

export default analyticsService;
