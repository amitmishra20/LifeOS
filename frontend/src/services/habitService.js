import api from './api';

export const habitService = {
  async getHabits(status) {
    const params = status ? { status } : {};
    const response = await api.get('/habits', { params });
    return response.data;
  },

  async getTodayHabits() {
    const response = await api.get('/habits/today');
    return response.data;
  },

  async getHabitById(id) {
    const response = await api.get(`/habits/${id}`);
    return response.data;
  },

  async createHabit(payload) {
    const response = await api.post('/habits', payload);
    return response.data;
  },

  async updateHabit(id, payload) {
    const response = await api.put(`/habits/${id}`, payload);
    return response.data;
  },

  async updateHabitStatus(id, status) {
    const response = await api.patch(`/habits/${id}/status`, { status });
    return response.data;
  },

  async deleteHabit(id) {
    const response = await api.delete(`/habits/${id}`);
    return response.data;
  },

  async toggleHabit(id, date) {
    const url = date ? `/habits/${id}/toggle?date=${date}` : `/habits/${id}/toggle`;
    const response = await api.post(url);
    return response.data;
  },

  async getHabitHistory(id) {
    const response = await api.get(`/habits/${id}/history`);
    return response.data;
  },
};

export default habitService;
