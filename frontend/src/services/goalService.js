import api from './api';

export const goalService = {
  async getGoals(status) {
    const params = status ? { status } : {};
    const response = await api.get('/goals', { params });
    return response.data;
  },

  async getGoalById(id) {
    const response = await api.get(`/goals/${id}`);
    return response.data;
  },

  async createGoal(payload) {
    const response = await api.post('/goals', payload);
    return response.data;
  },

  async updateGoal(id, payload) {
    const response = await api.put(`/goals/${id}`, payload);
    return response.data;
  },

  async deleteGoal(id) {
    const response = await api.delete(`/goals/${id}`);
    return response.data;
  },
};

export default goalService;
