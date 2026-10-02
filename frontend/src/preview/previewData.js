export const previewUser = {
  id: 'preview-user',
  name: 'Alex Morgan',
  email: 'alex@lifeos.local',
};

export const previewData = {
  user: previewUser,
  goals: [
    { id: 'goal-1', title: 'Build a calmer, more intentional life', category: 'Personal', progress: 68, status: 'ACTIVE', milestones: [] },
    { id: 'goal-2', title: 'Grow into a thoughtful technical leader', category: 'Career', progress: 42, status: 'ACTIVE', milestones: [] },
  ],
  tasks: [
    { id: 'task-1', title: 'Review weekly priorities', completed: false, dueDate: new Date().toISOString() },
    { id: 'task-2', title: 'Take a focused walk', completed: true, dueDate: new Date().toISOString() },
  ],
  habits: [
    { id: 'habit-1', name: 'Morning reflection', frequencyType: 'DAILY', currentStreak: 6, consistencyRate: 0.82, completedToday: true, history: [] },
    { id: 'habit-2', name: 'Read for 20 minutes', frequencyType: 'DAILY', currentStreak: 3, consistencyRate: 0.64, completedToday: false, history: [] },
  ],
  recommendations: { items: [{ id: 'focus-1', title: 'Protect your highest-energy hour', description: 'Use it for the work that matters most today.' }] },
};
