import api from './api';

export const userService = {
  async getProfileSummary() {
    const response = await api.get('/users/me');
    return response.data;
  },

  async deleteAccount() {
    const response = await api.delete('/users/me');
    return response.data;
  },
};

export default userService;
