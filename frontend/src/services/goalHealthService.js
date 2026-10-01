import api from './api';

export const goalHealthService = {
  async getGoalHealthOverview() {
    const response = await api.get('/goal-health');
    return response.data;
  },

  async getGoalHealth(goalId) {
    const response = await api.get(`/goal-health/${goalId}`);
    return response.data;
  },
};

export default goalHealthService;
