import api from './api';

export const taskService = {
  async getTasks(status) {
    const params = status ? { status } : {};
    const response = await api.get('/tasks', { params });
    return response.data;
  },

  async getTaskById(id) {
    const response = await api.get(`/tasks/${id}`);
    return response.data;
  },

  async createTask(payload) {
    const response = await api.post('/tasks', payload);
    return response.data;
  },

  async updateTask(id, payload) {
    const response = await api.put(`/tasks/${id}`, payload);
    return response.data;
  },

  async completeTask(id) {
    const response = await api.patch(`/tasks/${id}/complete`);
    return response.data;
  },

  async deleteTask(id) {
    const response = await api.delete(`/tasks/${id}`);
    return response.data;
  },
};

export default taskService;
