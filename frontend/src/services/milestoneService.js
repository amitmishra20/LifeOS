import api from './api';

export const milestoneService = {
  async getMilestonesByGoal(goalId) {
    const response = await api.get(`/goals/${goalId}/milestones`);
    return response.data;
  },

  async getMilestoneById(id) {
    const response = await api.get(`/milestones/${id}`);
    return response.data;
  },

  async createMilestone(goalId, payload) {
    const response = await api.post(`/goals/${goalId}/milestones`, payload);
    return response.data;
  },

  async updateMilestone(id, payload) {
    const response = await api.put(`/milestones/${id}`, payload);
    return response.data;
  },

  async deleteMilestone(id) {
    const response = await api.delete(`/milestones/${id}`);
    return response.data;
  },
};

export default milestoneService;
