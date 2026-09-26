import api from './api';

export const recommendationService = {
  async getDailyFocus() {
    const response = await api.get('/recommendations/daily-focus');
    return response.data;
  },
};

export default recommendationService;
