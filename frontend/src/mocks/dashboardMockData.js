/**
 * LifeOS High-Fidelity Mock Presentation Data v3
 * Source of Truth: Visual Reference & Phase 3 Visual Foundation
 * 
 * Strict Phase 3 Boundary:
 * Frontend visual and interaction demonstration.
 * Persona-rich, cohesive demonstration of LifeOS relational hierarchy.
 * Zero backend APIs, zero database mutations, zero real business logic.
 */

export const INITIAL_DASHBOARD_DATA = {
  overview: {
    userName: 'Amit',
    greeting: 'Good morning, Amit',
    headline: 'Build the life you want.',
    subtitle: 'Small, consistent actions create extraordinary results.',
    quote: 'Discipline today builds freedom tomorrow.',
    activeGoalsCount: 3,
    todayTasksCount: 5,
    habitsCount: 6,
    dateString: 'Tue, 23 Sep 2025',
    momentumScore: 72,
  },

  primaryFocus: {
    id: 1,
    title: 'Complete React Fundamentals',
    category: 'LEARNING',
    duration: '1h 30m',
    priority: 'HIGH PRIORITY',
    progress: 67,
    description:
      'Finish the React documentation section on components, props and state. This will strengthen your frontend foundation for upcoming projects.',
    actionLabel: 'Start Learning',
    completed: false,
  },

  timelineTasks: [
    {
      id: 1,
      title: 'Complete React Fundamentals',
      time: '10:00 AM',
      category: 'Learning',
      completed: true,
      active: false,
    },
    {
      id: 2,
      title: 'Build ShopSync UI Components',
      time: '1:00 PM',
      category: 'Project',
      completed: false,
      active: true,
    },
    {
      id: 3,
      title: 'Gym + Workout',
      time: '6:30 PM',
      category: 'Health',
      completed: false,
      active: false,
    },
    {
      id: 4,
      title: 'Read 20 pages',
      time: '9:00 PM',
      category: 'Personal',
      completed: false,
      active: false,
    },
  ],

  journeyWaypoints: [
    {
      id: 'current',
      label: 'Current Position',
      type: 'position',
      status: 'active',
      icon: 'dot',
    },
    {
      id: 'step-1',
      label: 'Build Skills',
      sublabel: 'Milestone',
      type: 'milestone',
      status: 'upcoming',
      icon: 'book',
    },
    {
      id: 'step-2',
      label: 'Get Internship',
      sublabel: 'Milestone',
      type: 'milestone',
      status: 'upcoming',
      icon: 'briefcase',
    },
    {
      id: 'step-3',
      label: 'Career Growth',
      sublabel: 'Goal',
      type: 'goal',
      status: 'destination',
      icon: 'flag',
    },
  ],

  goals: [
    {
      id: 101,
      title: 'Land a Software Internship',
      icon: 'code',
      milestonesInfo: '2/5 milestones',
      progress: 40,
      status: 'On Track',
      isAtRisk: false,
    },
    {
      id: 102,
      title: 'Stay Fit & Healthy',
      icon: 'heart',
      milestonesInfo: '1/4 milestones',
      progress: 25,
      status: 'On Track',
      isAtRisk: false,
    },
    {
      id: 103,
      title: 'Complete Java Full Stack',
      icon: 'education',
      milestonesInfo: '1/6 milestones',
      progress: 17,
      status: 'At Risk',
      isAtRisk: true,
    },
  ],

  quoteInspiration: {
    quote: "Progress isn't about perfection, it's about showing up.",
  },

  contextualGuidance: {
    title: 'Contextual Guidance',
    observation: "You've been most productive in the morning (10 AM - 1 PM) over the past 7 days.",
    whyItMatters: 'Your energy and focus are highest in the morning, which is ideal for deep work and learning.',
    suggestedFocus: 'Continue scheduling your most important learning and development tasks in the morning.',
    actionLabel: 'View Details',
  },

  weeklyRhythm: {
    title: "This Week's Rhythm",
    subtitle: 'Consistency builds momentum',
    days: [
      { key: 'm1', label: 'M', active: true },
      { key: 't1', label: 'T', active: true },
      { key: 'w1', label: 'W', active: true },
      { key: 't2', label: 'T', active: true },
      { key: 'f1', label: 'F', active: true },
      { key: 's1', label: 'S', active: false },
      { key: 's2', label: 'S', active: false },
    ],
    habits: [
      {
        id: 1,
        title: 'Morning Focus',
        score: '5/5 days',
        progress: 100,
        icon: 'book',
      },
      {
        id: 2,
        title: 'Hydration',
        score: '4/5 days',
        progress: 80,
        icon: 'water',
      },
      {
        id: 3,
        title: 'Workout',
        score: '3/5 days',
        progress: 60,
        icon: 'workout',
      },
    ],
  },

  recommendations: [
    {
      id: 'rec-1',
      title: 'Deep Work Session',
      description: 'Your focus is high this morning. Consider a 90-min deep work session.',
      icon: 'book',
    },
    {
      id: 'rec-2',
      title: 'Evening Walk',
      description: 'A short walk can boost your mood and recovery after workout.',
      icon: 'walk',
    },
  ],
};
