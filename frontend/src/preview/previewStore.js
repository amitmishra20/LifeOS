/**
 * LifeOS Design Preview In-Memory State Store
 * 
 * Manages mutable in-memory state during design preview sessions so that
 * user interactions (task toggle, habit logging, creating notes/tasks/goals)
 * respond dynamically without needing a backend server.
 */

import { createInitialPreviewDataset } from './previewData';

class PreviewStore {
  constructor() {
    this.reset();
  }

  reset() {
    this.data = createInitialPreviewDataset();
  }

  getUser() {
    return this.data.user;
  }

  getGoals(status) {
    if (status) {
      return this.data.goals.filter((g) => g.status === status);
    }
    return [...this.data.goals];
  }

  getGoalById(id) {
    const goal = this.data.goals.find((g) => g.id === Number(id));
    if (!goal) throw new Error('Goal not found');
    const milestones = this.data.milestones
      .filter((m) => m.goalId === Number(id))
      .sort((a, b) => a.orderIndex - b.orderIndex);
    return {
      ...goal,
      milestones,
    };
  }

  createGoal(payload) {
    const newGoal = {
      id: Date.now(),
      userId: 1,
      title: payload.title,
      description: payload.description || '',
      category: payload.category || 'GENERAL',
      priority: payload.priority || 'MEDIUM',
      status: 'ACTIVE',
      progress: 0,
      startDate: payload.startDate || null,
      targetDate: payload.targetDate || null,
      health: 'ON_TRACK',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    this.data.goals.unshift(newGoal);
    return newGoal;
  }

  updateGoal(id, payload) {
    const idx = this.data.goals.findIndex((g) => g.id === Number(id));
    if (idx === -1) throw new Error('Goal not found');
    this.data.goals[idx] = {
      ...this.data.goals[idx],
      ...payload,
      updatedAt: new Date().toISOString(),
    };
    return this.data.goals[idx];
  }

  deleteGoal(id) {
    this.data.goals = this.data.goals.filter((g) => g.id !== Number(id));
    this.data.milestones = this.data.milestones.filter((m) => m.goalId !== Number(id));
    return { message: 'Goal deleted successfully' };
  }

  getMilestonesByGoal(goalId) {
    return this.data.milestones
      .filter((m) => m.goalId === Number(goalId))
      .sort((a, b) => a.orderIndex - b.orderIndex);
  }

  getMilestoneById(id) {
    const m = this.data.milestones.find((item) => item.id === Number(id));
    if (!m) throw new Error('Milestone not found');
    return m;
  }

  createMilestone(goalId, payload) {
    const newM = {
      id: Date.now(),
      goalId: Number(goalId),
      userId: 1,
      title: payload.title,
      description: payload.description || '',
      targetDate: payload.targetDate || null,
      status: payload.status || 'PENDING',
      progress: 0,
      orderIndex: payload.orderIndex !== undefined ? payload.orderIndex : this.data.milestones.length,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    this.data.milestones.push(newM);
    return newM;
  }

  updateMilestone(id, payload) {
    const idx = this.data.milestones.findIndex((m) => m.id === Number(id));
    if (idx === -1) throw new Error('Milestone not found');
    this.data.milestones[idx] = {
      ...this.data.milestones[idx],
      ...payload,
      updatedAt: new Date().toISOString(),
    };
    return this.data.milestones[idx];
  }

  deleteMilestone(id) {
    this.data.milestones = this.data.milestones.filter((m) => m.id !== Number(id));
    return { message: 'Milestone deleted successfully' };
  }

  getTasks(status) {
    if (status) {
      return this.data.tasks.filter((t) => t.status === status);
    }
    return [...this.data.tasks];
  }

  getTaskById(id) {
    const task = this.data.tasks.find((t) => t.id === Number(id));
    if (!task) throw new Error('Task not found');
    return task;
  }

  createTask(payload) {
    const newTask = {
      id: Date.now(),
      userId: 1,
      goalId: payload.goalId || null,
      milestoneId: payload.milestoneId || null,
      goalTitle: payload.goalId ? (this.data.goals.find((g) => g.id === Number(payload.goalId))?.title || 'Goal') : null,
      milestoneTitle: payload.milestoneId ? (this.data.milestones.find((m) => m.id === Number(payload.milestoneId))?.title || 'Milestone') : null,
      title: payload.title,
      description: payload.description || '',
      priority: payload.priority || 'MEDIUM',
      status: 'TODO',
      dueDate: payload.dueDate || new Date().toISOString().split('T')[0],
      estimatedMinutes: payload.estimatedMinutes || null,
      completedAt: null,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    this.data.tasks.unshift(newTask);
    return newTask;
  }

  updateTask(id, payload) {
    const idx = this.data.tasks.findIndex((t) => t.id === Number(id));
    if (idx === -1) throw new Error('Task not found');
    this.data.tasks[idx] = {
      ...this.data.tasks[idx],
      ...payload,
      updatedAt: new Date().toISOString(),
    };
    return this.data.tasks[idx];
  }

  toggleTaskCompletion(id) {
    const task = this.data.tasks.find((t) => t.id === Number(id));
    if (!task) throw new Error('Task not found');
    const isNowCompleted = task.status !== 'COMPLETED';
    task.status = isNowCompleted ? 'COMPLETED' : 'TODO';
    task.completedAt = isNowCompleted ? new Date().toISOString() : null;
    task.updatedAt = new Date().toISOString();
    return task;
  }

  deleteTask(id) {
    this.data.tasks = this.data.tasks.filter((t) => t.id !== Number(id));
    return { message: 'Task deleted successfully' };
  }

  getHabits(status) {
    if (status) {
      return this.data.habits.filter((h) => h.status === status);
    }
    return [...this.data.habits];
  }

  getTodayHabits() {
    return [...this.data.habits];
  }

  getHabitById(id) {
    const h = this.data.habits.find((item) => item.id === Number(id));
    if (!h) throw new Error('Habit not found');
    return h;
  }

  createHabit(payload) {
    const todayStr = new Date().toISOString().split('T')[0];
    const newH = {
      id: Date.now(),
      userId: 1,
      goalId: payload.goalId || null,
      title: payload.title,
      description: payload.description || '',
      frequencyType: payload.frequencyType || 'DAILY',
      targetPerWeek: payload.targetPerWeek || 7,
      status: 'ACTIVE',
      currentStreak: 0,
      longestStreak: 0,
      weeklyConsistency: 0,
      completedToday: false,
      history: [
        { date: todayStr, completed: false, label: 'T' }
      ],
      createdAt: new Date().toISOString(),
    };
    this.data.habits.push(newH);
    return newH;
  }

  updateHabit(id, payload) {
    const idx = this.data.habits.findIndex((h) => h.id === Number(id));
    if (idx === -1) throw new Error('Habit not found');
    this.data.habits[idx] = {
      ...this.data.habits[idx],
      ...payload,
    };
    return this.data.habits[idx];
  }

  updateHabitStatus(id, status) {
    const habit = this.getHabitById(id);
    habit.status = status;
    return habit;
  }

  toggleHabit(id) {
    const habit = this.getHabitById(id);
    habit.completedToday = !habit.completedToday;
    if (habit.completedToday) {
      habit.currentStreak += 1;
      if (habit.currentStreak > habit.longestStreak) {
        habit.longestStreak = habit.currentStreak;
      }
    } else {
      habit.currentStreak = Math.max(0, habit.currentStreak - 1);
    }
    if (habit.history && habit.history.length > 0) {
      habit.history[habit.history.length - 1].completed = habit.completedToday;
    }
    return {
      completed: habit.completedToday,
      habit,
    };
  }

  getHabitHistory(id) {
    const habit = this.getHabitById(id);
    return habit.history || [];
  }

  deleteHabit(id) {
    this.data.habits = this.data.habits.filter((h) => h.id !== Number(id));
    return { message: 'Habit deleted successfully' };
  }

  getLearningItems(status) {
    if (status) {
      return this.data.learningItems.filter((item) => item.status === status);
    }
    return [...this.data.learningItems];
  }

  getLearningItem(id) {
    const item = this.data.learningItems.find((l) => l.id === Number(id));
    if (!item) throw new Error('Learning item not found');
    return item;
  }

  createLearningItem(payload) {
    const newItem = {
      id: Date.now(),
      userId: 1,
      goalId: payload.goalId || null,
      title: payload.title,
      description: payload.description || '',
      category: payload.category || 'TECHNICAL',
      targetProgress: payload.targetProgress || 100,
      currentProgress: payload.currentProgress || 0,
      status: 'ACTIVE',
      createdAt: new Date().toISOString(),
      sessionsCount: 0,
      totalMinutes: 0,
    };
    this.data.learningItems.push(newItem);
    return newItem;
  }

  updateLearningItem(id, payload) {
    const idx = this.data.learningItems.findIndex((l) => l.id === Number(id));
    if (idx === -1) throw new Error('Learning item not found');
    this.data.learningItems[idx] = {
      ...this.data.learningItems[idx],
      ...payload,
    };
    return this.data.learningItems[idx];
  }

  updateLearningProgress(id, currentProgress) {
    const item = this.getLearningItem(id);
    item.currentProgress = currentProgress;
    return item;
  }

  updateLearningStatus(id, status) {
    const item = this.getLearningItem(id);
    item.status = status;
    return item;
  }

  deleteLearningItem(id) {
    this.data.learningItems = this.data.learningItems.filter((l) => l.id !== Number(id));
    this.data.learningSessions = this.data.learningSessions.filter((s) => s.learningItemId !== Number(id));
    return { message: 'Learning item deleted successfully' };
  }

  createLearningSession(learningItemId, payload) {
    const item = this.getLearningItem(learningItemId);
    const newSession = {
      id: Date.now(),
      learningItemId: Number(learningItemId),
      userId: 1,
      sessionDate: payload.sessionDate || new Date().toISOString().split('T')[0],
      durationMinutes: payload.durationMinutes || 30,
      topic: payload.topic,
      notes: payload.notes || '',
      createdAt: new Date().toISOString(),
    };
    this.data.learningSessions.unshift(newSession);
    item.sessionsCount = (item.sessionsCount || 0) + 1;
    item.totalMinutes = (item.totalMinutes || 0) + newSession.durationMinutes;
    return newSession;
  }

  getLearningSessions(learningItemId) {
    return this.data.learningSessions.filter((s) => s.learningItemId === Number(learningItemId));
  }

  deleteLearningSession(learningItemId, sessionId) {
    this.data.learningSessions = this.data.learningSessions.filter((s) => s.id !== Number(sessionId));
    return { message: 'Learning session deleted successfully' };
  }

  getCalendarEvents() {
    return [...this.data.calendarEvents];
  }

  getUnifiedCalendarFeed() {
    const taskEvents = this.data.tasks.map((t) => ({
      id: `task-${t.id}`,
      entityType: 'TASK',
      title: t.title,
      date: t.dueDate,
      status: t.status,
      priority: t.priority,
      category: t.goalTitle || 'Task',
    }));

    const goalEvents = this.data.goals.map((g) => ({
      id: `goal-${g.id}`,
      entityType: 'GOAL',
      title: `Goal Target: ${g.title}`,
      date: g.targetDate,
      status: g.status,
      priority: g.priority,
      category: g.category,
    }));

    const customEvents = this.data.calendarEvents.map((e) => ({
      id: `event-${e.id}`,
      entityType: 'CUSTOM_EVENT',
      title: e.title,
      date: e.startTime.split('T')[0],
      startTime: e.startTime,
      endTime: e.endTime,
      description: e.description,
      category: 'Event',
    }));

    return [...taskEvents, ...goalEvents, ...customEvents];
  }

  getEvent(id) {
    const e = this.data.calendarEvents.find((event) => event.id === Number(id));
    if (!e) throw new Error('Event not found');
    return e;
  }

  createEvent(payload) {
    const newE = {
      id: Date.now(),
      userId: 1,
      title: payload.title,
      description: payload.description || '',
      startTime: payload.startTime,
      endTime: payload.endTime || null,
      eventType: payload.eventType || 'CUSTOM_EVENT',
      createdAt: new Date().toISOString(),
    };
    this.data.calendarEvents.push(newE);
    return newE;
  }

  updateEvent(id, payload) {
    const idx = this.data.calendarEvents.findIndex((e) => e.id === Number(id));
    if (idx === -1) throw new Error('Event not found');
    this.data.calendarEvents[idx] = {
      ...this.data.calendarEvents[idx],
      ...payload,
    };
    return this.data.calendarEvents[idx];
  }

  deleteEvent(id) {
    this.data.calendarEvents = this.data.calendarEvents.filter((e) => e.id !== Number(id));
    return { message: 'Event deleted successfully' };
  }

  getNotes(search, category) {
    return this.data.notes.filter((n) => {
      if (category && n.category !== category) return false;
      if (search) {
        const query = search.toLowerCase();
        return n.title.toLowerCase().includes(query) || (n.content && n.content.toLowerCase().includes(query));
      }
      return true;
    });
  }

  getNote(id) {
    const note = this.data.notes.find((n) => n.id === Number(id));
    if (!note) throw new Error('Note not found');
    return note;
  }

  createNote(payload) {
    const newNote = {
      id: Date.now(),
      userId: 1,
      title: payload.title,
      content: payload.content || '',
      category: payload.category || 'General',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    this.data.notes.unshift(newNote);
    return newNote;
  }

  updateNote(id, payload) {
    const idx = this.data.notes.findIndex((n) => n.id === Number(id));
    if (idx === -1) throw new Error('Note not found');
    this.data.notes[idx] = {
      ...this.data.notes[idx],
      ...payload,
      updatedAt: new Date().toISOString(),
    };
    return this.data.notes[idx];
  }

  deleteNote(id) {
    this.data.notes = this.data.notes.filter((n) => n.id !== Number(id));
    return { message: 'Note deleted successfully' };
  }

  getGoalHealthOverview() {
    return this.data.goalHealthOverview;
  }

  getGoalHealth(goalId) {
    const evalItem = this.data.goalHealthOverview.evaluations.find((e) => e.goalId === Number(goalId));
    if (!evalItem) throw new Error('Goal health evaluation not found');
    return evalItem;
  }

  getDailyFocus() {
    return this.data.dailyFocus;
  }

  getConsolidatedRecommendations() {
    return this.data.consolidatedRecommendations;
  }

  getAnalyticsDashboard() {
    return this.data.analyticsDashboard;
  }

  getProfileSummary() {
    return {
      ...this.data.profileSummary,
      totalGoals: this.data.goals.length,
      totalTasks: this.data.tasks.length,
      totalHabits: this.data.habits.length,
      totalLearningItems: this.data.learningItems.length,
      totalNotes: this.data.notes.length,
    };
  }
}

export const previewStore = new PreviewStore();
export default previewStore;
