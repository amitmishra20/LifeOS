/**
 * LifeOS Preview API Adapter
 * 
 * STRICTLY DEVELOPMENT-ONLY:
 * Intercepts Axios requests when preview mode is active and returns
 * deterministic mock responses from the in-memory PreviewStore.
 */

import previewStore from './previewStore';

export const handlePreviewRequest = async (config) => {
  const url = (config.url || '').replace(/^\/api\/v1/, '').replace(/^\//, '');
  const method = (config.method || 'get').toLowerCase();
  const data = typeof config.data === 'string' ? JSON.parse(config.data || '{}') : (config.data || {});
  const params = config.params || {};

  // Helper mock response
  const mockOk = (responseData, status = 200) => ({
    data: responseData,
    status,
    statusText: 'OK',
    headers: {},
    config,
  });

  // 1. Auth & CSRF Endpoints
  if (url === 'auth/me' && method === 'get') {
    return mockOk(previewStore.getUser());
  }
  if (url === 'auth/csrf' && method === 'get') {
    return mockOk({ token: 'preview-csrf-token', headerName: 'X-XSRF-TOKEN', parameterName: '_csrf' });
  }
  if (url === 'auth/login' && method === 'post') {
    return mockOk(previewStore.getUser());
  }
  if (url === 'auth/register' && method === 'post') {
    return mockOk(previewStore.getUser(), 201);
  }
  if (url === 'auth/logout' && method === 'post') {
    return mockOk({ message: 'Logged out successfully' });
  }

  // 2. Health
  if (url === 'health') {
    return mockOk({ status: 'UP', message: 'LifeOS Preview Environment Active' });
  }

  // 3. Goals
  if (url === 'goals' && method === 'get') {
    return mockOk(previewStore.getGoals(params.status));
  }
  if (url.match(/^goals\/\d+$/) && method === 'get') {
    const id = url.split('/')[1];
    return mockOk(previewStore.getGoalById(id));
  }
  if (url === 'goals' && method === 'post') {
    return mockOk(previewStore.createGoal(data), 201);
  }
  if (url.match(/^goals\/\d+$/) && method === 'put') {
    const id = url.split('/')[1];
    return mockOk(previewStore.updateGoal(id, data));
  }
  if (url.match(/^goals\/\d+$/) && method === 'delete') {
    const id = url.split('/')[1];
    return mockOk(previewStore.deleteGoal(id));
  }

  // 4. Milestones
  if (url.match(/^goals\/\d+\/milestones$/) && method === 'get') {
    const goalId = url.split('/')[1];
    return mockOk(previewStore.getMilestonesByGoal(goalId));
  }
  if (url.match(/^goals\/\d+\/milestones$/) && method === 'post') {
    const goalId = url.split('/')[1];
    return mockOk(previewStore.createMilestone(goalId, data), 201);
  }
  if (url.match(/^milestones\/\d+$/) && method === 'get') {
    const id = url.split('/')[1];
    return mockOk(previewStore.getMilestoneById(id));
  }
  if (url.match(/^milestones\/\d+$/) && method === 'put') {
    const id = url.split('/')[1];
    return mockOk(previewStore.updateMilestone(id, data));
  }
  if (url.match(/^milestones\/\d+$/) && method === 'delete') {
    const id = url.split('/')[1];
    return mockOk(previewStore.deleteMilestone(id));
  }

  // 5. Tasks
  if (url === 'tasks' && method === 'get') {
    return mockOk(previewStore.getTasks(params.status));
  }
  if (url.match(/^tasks\/\d+$/) && method === 'get') {
    const id = url.split('/')[1];
    return mockOk(previewStore.getTaskById(id));
  }
  if (url === 'tasks' && method === 'post') {
    return mockOk(previewStore.createTask(data), 201);
  }
  if (url.match(/^tasks\/\d+$/) && method === 'put') {
    const id = url.split('/')[1];
    return mockOk(previewStore.updateTask(id, data));
  }
  if (url.match(/^tasks\/\d+\/complete$/) && method === 'patch') {
    const id = url.split('/')[1];
    return mockOk(previewStore.toggleTaskCompletion(id));
  }
  if (url.match(/^tasks\/\d+$/) && method === 'delete') {
    const id = url.split('/')[1];
    return mockOk(previewStore.deleteTask(id));
  }

  // 6. Habits
  if (url === 'habits' && method === 'get') {
    return mockOk(previewStore.getHabits(params.status));
  }
  if (url === 'habits/today' && method === 'get') {
    return mockOk(previewStore.getTodayHabits());
  }
  if (url.match(/^habits\/\d+$/) && method === 'get') {
    const id = url.split('/')[1];
    return mockOk(previewStore.getHabitById(id));
  }
  if (url === 'habits' && method === 'post') {
    return mockOk(previewStore.createHabit(data), 201);
  }
  if (url.match(/^habits\/\d+$/) && method === 'put') {
    const id = url.split('/')[1];
    return mockOk(previewStore.updateHabit(id, data));
  }
  if (url.match(/^habits\/\d+\/status$/) && method === 'patch') {
    const id = url.split('/')[1];
    return mockOk(previewStore.updateHabitStatus(id, data.status));
  }
  if (url.match(/^habits\/\d+\/toggle/) && method === 'post') {
    const id = url.split('/')[1].split('?')[0];
    return mockOk(previewStore.toggleHabit(id));
  }
  if (url.match(/^habits\/\d+\/history$/) && method === 'get') {
    const id = url.split('/')[1];
    return mockOk(previewStore.getHabitHistory(id));
  }
  if (url.match(/^habits\/\d+$/) && method === 'delete') {
    const id = url.split('/')[1];
    return mockOk(previewStore.deleteHabit(id));
  }

  // 7. Learning
  if (url === 'learning' && method === 'get') {
    return mockOk(previewStore.getLearningItems(params.status));
  }
  if (url.match(/^learning\/\d+$/) && method === 'get') {
    const id = url.split('/')[1];
    return mockOk(previewStore.getLearningItem(id));
  }
  if (url === 'learning' && method === 'post') {
    return mockOk(previewStore.createLearningItem(data), 201);
  }
  if (url.match(/^learning\/\d+$/) && method === 'put') {
    const id = url.split('/')[1];
    return mockOk(previewStore.updateLearningItem(id, data));
  }
  if (url.match(/^learning\/\d+\/progress$/) && method === 'patch') {
    const id = url.split('/')[1];
    return mockOk(previewStore.updateLearningProgress(id, data.currentProgress));
  }
  if (url.match(/^learning\/\d+\/status$/) && method === 'patch') {
    const id = url.split('/')[1];
    return mockOk(previewStore.updateLearningStatus(id, data.status));
  }
  if (url.match(/^learning\/\d+$/) && method === 'delete') {
    const id = url.split('/')[1];
    return mockOk(previewStore.deleteLearningItem(id));
  }
  if (url.match(/^learning\/\d+\/sessions$/) && method === 'get') {
    const itemId = url.split('/')[1];
    return mockOk(previewStore.getLearningSessions(itemId));
  }
  if (url.match(/^learning\/\d+\/sessions$/) && method === 'post') {
    const itemId = url.split('/')[1];
    return mockOk(previewStore.createLearningSession(itemId, data), 201);
  }
  if (url.match(/^learning\/\d+\/sessions\/\d+$/) && method === 'delete') {
    const parts = url.split('/');
    const itemId = parts[1];
    const sessionId = parts[3];
    return mockOk(previewStore.deleteLearningSession(itemId, sessionId));
  }

  // 8. Calendar
  if (url === 'calendar' && method === 'get') {
    return mockOk(previewStore.getUnifiedCalendarFeed());
  }
  if (url === 'calendar/events' && method === 'get') {
    return mockOk(previewStore.getCalendarEvents());
  }
  if (url.match(/^calendar\/events\/\d+$/) && method === 'get') {
    const id = url.split('/')[2];
    return mockOk(previewStore.getEvent(id));
  }
  if (url === 'calendar/events' && method === 'post') {
    return mockOk(previewStore.createEvent(data), 201);
  }
  if (url.match(/^calendar\/events\/\d+$/) && method === 'put') {
    const id = url.split('/')[2];
    return mockOk(previewStore.updateEvent(id, data));
  }
  if (url.match(/^calendar\/events\/\d+$/) && method === 'delete') {
    const id = url.split('/')[2];
    return mockOk(previewStore.deleteEvent(id));
  }

  // 9. Notes
  if (url === 'notes' && method === 'get') {
    return mockOk(previewStore.getNotes(params.search, params.category));
  }
  if (url.match(/^notes\/\d+$/) && method === 'get') {
    const id = url.split('/')[1];
    return mockOk(previewStore.getNote(id));
  }
  if (url === 'notes' && method === 'post') {
    return mockOk(previewStore.createNote(data), 201);
  }
  if (url.match(/^notes\/\d+$/) && method === 'put') {
    const id = url.split('/')[1];
    return mockOk(previewStore.updateNote(id, data));
  }
  if (url.match(/^notes\/\d+$/) && method === 'delete') {
    const id = url.split('/')[1];
    return mockOk(previewStore.deleteNote(id));
  }

  // 10. Analytics
  if (url === 'analytics/dashboard' && method === 'get') {
    return mockOk(previewStore.getAnalyticsDashboard());
  }
  if (url === 'analytics/productivity' && method === 'get') {
    return mockOk(previewStore.getAnalyticsDashboard().productivity);
  }
  if (url === 'analytics/goals' && method === 'get') {
    return mockOk(previewStore.getAnalyticsDashboard().goals);
  }
  if (url === 'analytics/habits' && method === 'get') {
    return mockOk(previewStore.getAnalyticsDashboard().habits);
  }

  // 11. Goal Health
  if (url === 'goal-health' && method === 'get') {
    return mockOk(previewStore.getGoalHealthOverview());
  }
  if (url.match(/^goal-health\/\d+$/) && method === 'get') {
    const goalId = url.split('/')[1];
    return mockOk(previewStore.getGoalHealth(goalId));
  }

  // 12. Recommendations
  if (url === 'recommendations/daily-focus' && method === 'get') {
    return mockOk(previewStore.getDailyFocus());
  }
  if (url === 'recommendations' && method === 'get') {
    return mockOk(previewStore.getConsolidatedRecommendations());
  }

  // 13. Users
  if (url === 'users/me' && method === 'get') {
    return mockOk(previewStore.getProfileSummary());
  }
  if (url === 'users/me' && method === 'delete') {
    return mockOk({ message: 'Account deleted' });
  }

  // Fallback default mock
  console.warn(`[LifeOS Design Preview] Unhandled route: ${method.toUpperCase()} /${url}`);
  return mockOk({});
};
