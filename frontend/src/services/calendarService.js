import api from './api';

export const calendarService = {
  async getEvents(params = {}) {
    const response = await api.get('/calendar/events', { params });
    return response.data;
  },

  async getUnifiedFeed(startDate, endDate) {
    const params = {};
    if (startDate) params.start = startDate;
    if (endDate) params.end = endDate;
    const response = await api.get('/calendar', { params });
    return response.data;
  },

  async getEvent(id) {
    const response = await api.get(`/calendar/events/${id}`);
    return response.data;
  },

  async createEvent(payload) {
    const response = await api.post('/calendar/events', payload);
    return response.data;
  },

  async updateEvent(id, payload) {
    const response = await api.put(`/calendar/events/${id}`, payload);
    return response.data;
  },

  async deleteEvent(id) {
    const response = await api.delete(`/calendar/events/${id}`);
    return response.data;
  },
};

export default calendarService;
