import api from './api';

export const learningService = {
  async getLearningItems(status) {
    const params = status ? { status } : {};
    const response = await api.get('/learning', { params });
    return response.data;
  },

  async getLearningItem(id) {
    const response = await api.get(`/learning/${id}`);
    return response.data;
  },

  async createLearningItem(payload) {
    const response = await api.post('/learning', payload);
    return response.data;
  },

  async updateLearningItem(id, payload) {
    const response = await api.put(`/learning/${id}`, payload);
    return response.data;
  },

  async updateLearningProgress(id, currentProgress) {
    const response = await api.patch(`/learning/${id}/progress`, { currentProgress });
    return response.data;
  },

  async updateLearningStatus(id, status) {
    const response = await api.patch(`/learning/${id}/status`, { status });
    return response.data;
  },

  async deleteLearningItem(id) {
    const response = await api.delete(`/learning/${id}`);
    return response.data;
  },

  async createLearningSession(learningItemId, payload) {
    const response = await api.post(`/learning/${learningItemId}/sessions`, payload);
    return response.data;
  },

  async getLearningSessions(learningItemId) {
    const response = await api.get(`/learning/${learningItemId}/sessions`);
    return response.data;
  },

  async deleteLearningSession(learningItemId, sessionId) {
    const response = await api.delete(`/learning/${learningItemId}/sessions/${sessionId}`);
    return response.data;
  },
};

export default learningService;
