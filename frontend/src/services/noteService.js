import api from './api';

export const noteService = {
  async getNotes(params = {}) {
    const response = await api.get('/notes', { params });
    return response.data;
  },

  async getNote(id) {
    const response = await api.get(`/notes/${id}`);
    return response.data;
  },

  async createNote(payload) {
    const response = await api.post('/notes', payload);
    return response.data;
  },

  async updateNote(id, payload) {
    const response = await api.put(`/notes/${id}`, payload);
    return response.data;
  },

  async deleteNote(id) {
    const response = await api.delete(`/notes/${id}`);
    return response.data;
  },
};

export default noteService;
