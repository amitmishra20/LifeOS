/**
 * LifeOS Design Preview Dataset
 * 
 * STRICTLY FOR FRONTEND VISUAL / DESIGN PREVIEW IN DEVELOPMENT.
 * Narrative: Computer Science student preparing for software internships.
 */

export const createInitialPreviewDataset = () => {
  const todayStr = new Date().toISOString().split('T')[0];
  const yesterdayStr = new Date(Date.now() - 86400000).toISOString().split('T')[0];
  const tomorrowStr = new Date(Date.now() + 86400000).toISOString().split('T')[0];
  const futureStr = new Date(Date.now() + 86400000 * 3).toISOString().split('T')[0];

  const user = {
    id: 1,
    name: 'Alex Chen',
    email: 'alex.chen@university.edu',
    createdAt: '2026-09-01T08:00:00Z',
    role: 'ROLE_USER',
  };

  const goals = [
    {
      id: 1,
      userId: 1,
      title: 'Land a Software Internship',
      description: 'Comprehensive preparation across Data Structures, Algorithms, Java Full Stack, and Portfolio Projects.',
      category: 'CAREER',
      priority: 'HIGH',
      status: 'ACTIVE',
      progress: 45,
      startDate: '2026-09-01',
      targetDate: '2026-12-15',
      health: 'ON_TRACK',
      createdAt: '2026-09-01T08:00:00Z',
      updatedAt: '2026-10-01T10:00:00Z',
    },
    {
      id: 2,
      userId: 1,
      title: 'Maintain 3.8+ GPA in Computer Science',
      description: 'Excel in Operating Systems, Database Management Systems, and Distributed Networks coursework.',
      category: 'ACADEMIC',
      priority: 'MEDIUM',
      status: 'ACTIVE',
      progress: 60,
      startDate: '2026-08-20',
      targetDate: '2026-12-20',
      health: 'ON_TRACK',
      createdAt: '2026-08-20T08:00:00Z',
      updatedAt: '2026-10-01T10:00:00Z',
    }
  ];

  const milestones = [
    {
      id: 1,
      goalId: 1,
      userId: 1,
      title: 'DSA Preparation',
      description: 'Master Arrays, Linked Lists, Trees, Graphs, Dynamic Programming, and Recursion.',
      targetDate: '2026-10-01',
      status: 'COMPLETED',
      progress: 100,
      orderIndex: 0,
      createdAt: '2026-09-01T08:30:00Z',
      updatedAt: '2026-10-01T09:00:00Z',
    },
    {
      id: 2,
      goalId: 1,
      userId: 1,
      title: 'Java Full Stack',
      description: 'Build robust REST APIs using Spring Boot, Spring Security, Hibernate JPA, and React.',
      targetDate: '2026-10-25',
      status: 'IN_PROGRESS',
      progress: 60,
      orderIndex: 1,
      createdAt: '2026-09-01T08:30:00Z',
      updatedAt: '2026-10-01T09:00:00Z',
    },
    {
      id: 3,
      goalId: 1,
      userId: 1,
      title: 'Projects & Portfolio',
      description: 'Deliver LifeOS Personal Operating System and a high-concurrency distributed cache project.',
      targetDate: '2026-11-15',
      status: 'IN_PROGRESS',
      progress: 30,
      orderIndex: 2,
      createdAt: '2026-09-01T08:30:00Z',
      updatedAt: '2026-10-01T09:00:00Z',
    },
    {
      id: 4,
      goalId: 1,
      userId: 1,
      title: 'Interview Preparation',
      description: 'Mock behavioral interviews, system design walkthroughs, and resume polish.',
      targetDate: '2026-12-10',
      status: 'PENDING',
      progress: 0,
      orderIndex: 3,
      createdAt: '2026-09-01T08:30:00Z',
      updatedAt: '2026-10-01T09:00:00Z',
    },
  ];

  const tasks = [
    {
      id: 1,
      userId: 1,
      goalId: 1,
      milestoneId: 1,
      goalTitle: 'Land a Software Internship',
      milestoneTitle: 'DSA Preparation',
      title: 'Complete HashMap practice',
      description: 'Implement two-sum variations and custom hash table collision resolution techniques.',
      priority: 'HIGH',
      status: 'COMPLETED',
      dueDate: todayStr,
      estimatedMinutes: 45,
      completedAt: yesterdayStr + 'T18:30:00Z',
      createdAt: '2026-09-28T10:00:00Z',
      updatedAt: '2026-10-01T18:30:00Z',
    },
    {
      id: 2,
      userId: 1,
      goalId: 1,
      milestoneId: 1,
      goalTitle: 'Land a Software Internship',
      milestoneTitle: 'DSA Preparation',
      title: "Solve today's DSA problems",
      description: 'Solve 2 medium LeetCode questions focusing on graph depth-first search and topological sort.',
      priority: 'CRITICAL',
      status: 'TODO',
      dueDate: todayStr,
      estimatedMinutes: 50,
      completedAt: null,
      createdAt: '2026-09-29T11:00:00Z',
      updatedAt: '2026-10-01T09:00:00Z',
    },
    {
      id: 3,
      userId: 1,
      goalId: 1,
      milestoneId: 2,
      goalTitle: 'Land a Software Internship',
      milestoneTitle: 'Java Full Stack',
      title: 'Finish Java collections lesson',
      description: 'Deep dive into ConcurrentHashMap internals, Iterator fail-fast vs fail-safe behavior.',
      priority: 'MEDIUM',
      status: 'TODO',
      dueDate: todayStr,
      estimatedMinutes: 40,
      completedAt: null,
      createdAt: '2026-09-30T14:00:00Z',
      updatedAt: '2026-10-01T09:00:00Z',
    },
    {
      id: 4,
      userId: 1,
      goalId: 1,
      milestoneId: 3,
      goalTitle: 'Land a Software Internship',
      milestoneTitle: 'Projects & Portfolio',
      title: 'Implement a backend feature',
      description: 'Build the Goal Health evaluation matrix and transactional cascade deletion logic.',
      priority: 'HIGH',
      status: 'TODO',
      dueDate: tomorrowStr,
      estimatedMinutes: 90,
      completedAt: null,
      createdAt: '2026-10-01T10:00:00Z',
      updatedAt: '2026-10-01T10:00:00Z',
    },
    {
      id: 5,
      userId: 1,
      goalId: 1,
      milestoneId: 4,
      goalTitle: 'Land a Software Internship',
      milestoneTitle: 'Interview Preparation',
      title: 'Review interview questions',
      description: 'Practice the STAR method on top behavioral interview scenarios and project retrospectives.',
      priority: 'LOW',
      status: 'TODO',
      dueDate: futureStr,
      estimatedMinutes: 30,
      completedAt: null,
      createdAt: '2026-10-01T11:00:00Z',
      updatedAt: '2026-10-01T11:00:00Z',
    }
  ];

  const habits = [
    {
      id: 1,
      userId: 1,
      goalId: 1,
      title: 'DSA Practice',
      description: 'Solve at least one algorithmic challenge daily to maintain problem-solving speed.',
      frequencyType: 'DAILY',
      targetPerWeek: 7,
      status: 'ACTIVE',
      currentStreak: 12,
      longestStreak: 18,
      weeklyConsistency: 100,
      completedToday: true,
      history: [
        { date: new Date(Date.now() - 86400000 * 6).toISOString().split('T')[0], completed: true, label: 'M' },
        { date: new Date(Date.now() - 86400000 * 5).toISOString().split('T')[0], completed: true, label: 'T' },
        { date: new Date(Date.now() - 86400000 * 4).toISOString().split('T')[0], completed: true, label: 'W' },
        { date: new Date(Date.now() - 86400000 * 3).toISOString().split('T')[0], completed: true, label: 'T' },
        { date: new Date(Date.now() - 86400000 * 2).toISOString().split('T')[0], completed: true, label: 'F' },
        { date: yesterdayStr, completed: true, label: 'S' },
        { date: todayStr, completed: true, label: 'S' },
      ],
      createdAt: '2026-09-01T08:00:00Z',
    },
    {
      id: 2,
      userId: 1,
      goalId: 1,
      title: 'Java Practice',
      description: 'Write 45 minutes of idiomatic Java 17+ code, testing modern APIs and patterns.',
      frequencyType: 'WEEKLY',
      targetPerWeek: 5,
      status: 'ACTIVE',
      currentStreak: 8,
      longestStreak: 14,
      weeklyConsistency: 85,
      completedToday: true,
      history: [
        { date: new Date(Date.now() - 86400000 * 6).toISOString().split('T')[0], completed: true, label: 'M' },
        { date: new Date(Date.now() - 86400000 * 5).toISOString().split('T')[0], completed: true, label: 'T' },
        { date: new Date(Date.now() - 86400000 * 4).toISOString().split('T')[0], completed: false, label: 'W' },
        { date: new Date(Date.now() - 86400000 * 3).toISOString().split('T')[0], completed: true, label: 'T' },
        { date: new Date(Date.now() - 86400000 * 2).toISOString().split('T')[0], completed: true, label: 'F' },
        { date: yesterdayStr, completed: true, label: 'S' },
        { date: todayStr, completed: true, label: 'S' },
      ],
      createdAt: '2026-09-01T08:00:00Z',
    },
    {
      id: 3,
      userId: 1,
      goalId: null,
      title: 'Exercise & Running',
      description: 'Physical training and cardio for stamina and mental recovery.',
      frequencyType: 'WEEKLY',
      targetPerWeek: 4,
      status: 'ACTIVE',
      currentStreak: 4,
      longestStreak: 9,
      weeklyConsistency: 75,
      completedToday: false,
      history: [
        { date: new Date(Date.now() - 86400000 * 6).toISOString().split('T')[0], completed: true, label: 'M' },
        { date: new Date(Date.now() - 86400000 * 5).toISOString().split('T')[0], completed: false, label: 'T' },
        { date: new Date(Date.now() - 86400000 * 4).toISOString().split('T')[0], completed: true, label: 'W' },
        { date: new Date(Date.now() - 86400000 * 3).toISOString().split('T')[0], completed: false, label: 'T' },
        { date: new Date(Date.now() - 86400000 * 2).toISOString().split('T')[0], completed: true, label: 'F' },
        { date: yesterdayStr, completed: true, label: 'S' },
        { date: todayStr, completed: false, label: 'S' },
      ],
      createdAt: '2026-09-01T08:00:00Z',
    }
  ];

  const learningItems = [
    {
      id: 1,
      userId: 1,
      goalId: 1,
      title: 'Java',
      description: 'Core concepts, JVM internals, memory model, Concurrency, and Stream pipelines.',
      category: 'TECHNICAL',
      targetProgress: 100,
      currentProgress: 75,
      status: 'ACTIVE',
      createdAt: '2026-09-01T09:00:00Z',
      sessionsCount: 14,
      totalMinutes: 620,
    },
    {
      id: 2,
      userId: 1,
      goalId: 1,
      title: 'Spring Boot',
      description: 'IoC, Auto-configuration, WebMVC, Spring Security 6, JPA, and Actuator.',
      category: 'TECHNICAL',
      targetProgress: 100,
      currentProgress: 60,
      status: 'ACTIVE',
      createdAt: '2026-09-05T09:00:00Z',
      sessionsCount: 9,
      totalMinutes: 480,
    },
    {
      id: 3,
      userId: 1,
      goalId: 1,
      title: 'React',
      description: 'Component lifecycle, Context architecture, Hooks, Vite, and performance optimization.',
      category: 'TECHNICAL',
      targetProgress: 100,
      currentProgress: 70,
      status: 'ACTIVE',
      createdAt: '2026-09-08T09:00:00Z',
      sessionsCount: 11,
      totalMinutes: 530,
    },
    {
      id: 4,
      userId: 1,
      goalId: 1,
      title: 'SQL',
      description: 'Relational design, normalization, B-Tree indexes, execution plans, and transactions.',
      category: 'TECHNICAL',
      targetProgress: 100,
      currentProgress: 85,
      status: 'ACTIVE',
      createdAt: '2026-09-02T09:00:00Z',
      sessionsCount: 8,
      totalMinutes: 390,
    }
  ];

  const learningSessions = [
    {
      id: 1,
      learningItemId: 1,
      userId: 1,
      sessionDate: yesterdayStr,
      durationMinutes: 45,
      topic: 'Java Concurrency & Virtual Threads',
      notes: 'Reviewed ExecutorService, ForkJoinPool, and Project Loom virtual thread mechanics.',
      createdAt: yesterdayStr + 'T17:00:00Z',
    },
    {
      id: 2,
      learningItemId: 2,
      userId: 1,
      sessionDate: yesterdayStr,
      durationMinutes: 60,
      topic: 'Spring Security Filter Chains & CSRF',
      notes: 'Implemented SpaCsrfTokenRequestHandler and HttpOnly cookie authentication strategy.',
      createdAt: yesterdayStr + 'T19:00:00Z',
    },
    {
      id: 3,
      learningItemId: 3,
      userId: 1,
      sessionDate: todayStr,
      durationMinutes: 40,
      topic: 'React Responsive Architecture & Spatial Layouts',
      notes: 'Constructed responsive tablet rail and touch-optimized bottom sheets.',
      createdAt: todayStr + 'T09:30:00Z',
    }
  ];

  const calendarEvents = [
    {
      id: 1,
      userId: 1,
      title: 'Campus Mock Technical Interview',
      description: 'Live whiteboard coding session with CSE alumni mentors.',
      startTime: todayStr + 'T15:00:00',
      endTime: todayStr + 'T16:00:00',
      eventType: 'CUSTOM_EVENT',
      createdAt: '2026-09-25T10:00:00Z',
    },
    {
      id: 2,
      userId: 1,
      title: 'Algorithms Workshop & Problem Solving',
      description: 'Competitive programming study group on tree & graph algorithms.',
      startTime: tomorrowStr + 'T17:30:00',
      endTime: tomorrowStr + 'T19:00:00',
      eventType: 'CUSTOM_EVENT',
      createdAt: '2026-09-26T10:00:00Z',
    }
  ];

  const notes = [
    {
      id: 1,
      userId: 1,
      title: 'DSA Core Patterns',
      category: 'Algorithms',
      content: 'Sliding window technique, two pointers, monotonic stack, binary search invariants, and topological sorting recipes for competitive placement rounds.',
      createdAt: '2026-09-20T11:00:00Z',
      updatedAt: '2026-09-29T14:30:00Z',
    },
    {
      id: 2,
      userId: 1,
      title: 'Spring Security Filter Notes',
      category: 'Backend',
      content: 'FilterChain proxy execution order, OncePerRequestFilter guarantees, UserPrincipal contract, and stateless SessionCreationPolicy configuration.',
      createdAt: '2026-09-22T15:00:00Z',
      updatedAt: '2026-10-01T12:00:00Z',
    },
    {
      id: 3,
      userId: 1,
      title: 'System Design Fundamentals',
      category: 'Architecture',
      content: 'Database indexing strategies (B-Tree vs Hash), caching patterns (write-through vs cache-aside), horizontal partition sharding, and CAP theorem trade-offs.',
      createdAt: '2026-09-25T09:00:00Z',
      updatedAt: '2026-10-01T16:00:00Z',
    }
  ];

  const goalHealthOverview = {
    totalGoals: 2,
    onTrackCount: 2,
    atRiskCount: 0,
    behindCount: 0,
    completedCount: 0,
    evaluations: [
      {
        goalId: 1,
        goalTitle: 'Land a Software Internship',
        category: 'CAREER',
        status: 'ACTIVE',
        health: 'ON_TRACK',
        actualProgress: 45.0,
        expectedProgress: 42.0,
        delta: 3.0,
        startDate: '2026-09-01',
        targetDate: '2026-12-15',
        effectiveStartDate: '2026-09-01',
        totalDays: 105,
        elapsedDays: 31,
        remainingDays: 74,
        reason: 'Progress (45.0%) meets or exceeds expected progress (42.0%).',
      },
      {
        goalId: 2,
        goalTitle: 'Maintain 3.8+ GPA in Computer Science',
        category: 'ACADEMIC',
        status: 'ACTIVE',
        health: 'ON_TRACK',
        actualProgress: 60.0,
        expectedProgress: 55.0,
        delta: 5.0,
        startDate: '2026-08-20',
        targetDate: '2026-12-20',
        effectiveStartDate: '2026-08-20',
        totalDays: 122,
        elapsedDays: 43,
        remainingDays: 79,
        reason: 'Progress (60.0%) meets or exceeds expected progress (55.0%).',
      }
    ]
  };

  const analyticsDashboard = {
    productivity: {
      score: 82,
      status: 'High Momentum',
      breakdown: [
        { component: 'Task Completion', weight: 0.30, rawScore: 80, weightedScore: 24.0 },
        { component: 'Habit Consistency', weight: 0.25, rawScore: 88, weightedScore: 22.0 },
        { component: 'Goal Progress', weight: 0.25, rawScore: 75, weightedScore: 18.75 },
        { component: 'Learning Activity', weight: 0.20, rawScore: 86, weightedScore: 17.2 },
      ]
    },
    goals: {
      totalGoals: 2,
      activeGoals: 2,
      totalMilestones: 4,
      completedMilestones: 1,
      overallProgress: 45.0,
    },
    tasks: {
      totalTasks: 5,
      completedTasks: 1,
      pendingTasks: 4,
      completionRate: 80.0,
      weeklyCompleted: [3, 4, 2, 5, 4, 6, 4],
    },
    habits: {
      activeHabitsCount: 3,
      consistencyRate: 88.0,
      perfectDaysCount: 5,
    },
    learning: {
      activeItemsCount: 4,
      completedItemsCount: 0,
      totalHoursLogged: 33.6,
      sessionsThisWeek: 6,
    }
  };

  const dailyFocus = {
    date: todayStr,
    primaryFocus: {
      task: tasks[1], // Solve today's DSA problems
      score: 95,
      priorityRank: 1,
      whyReason: 'High priority task linked to your active DSA Preparation milestone.',
      goal: goals[0],
      milestone: milestones[0],
    },
    items: [
      {
        task: tasks[1],
        score: 95,
        priorityRank: 1,
        primaryReason: 'High priority milestone task required for interview readiness.',
        goal: goals[0],
        milestone: milestones[0],
      },
      {
        task: tasks[2],
        score: 80,
        priorityRank: 2,
        primaryReason: 'Daily practice task aligned with Java Full Stack development.',
        goal: goals[0],
        milestone: milestones[1],
      },
      {
        task: tasks[3],
        score: 75,
        priorityRank: 3,
        primaryReason: 'Advancement task on your LifeOS capstone project.',
        goal: goals[0],
        milestone: milestones[2],
      }
    ],
    supportingHabits: [
      habits[0],
      habits[1],
    ],
    recommendedLearning: [
      learningItems[0],
      learningItems[1],
    ]
  };

  const consolidatedRecommendations = {
    date: todayStr,
    dailyFocus,
    recommendations: [
      {
        id: 1,
        type: 'FOCUS_ALIGNMENT',
        title: "Solve today's DSA problems",
        description: 'Consistent algorithmic practice is your strongest placement multiplier.',
        impact: 'HIGH',
        category: 'CAREER',
        actionUrl: '/tasks',
      },
      {
        id: 2,
        type: 'HABIT_CONSISTENCY',
        title: 'Maintain your 12-day DSA streak',
        description: 'You are 2 days away from a two-week unbroken consistency milestone.',
        impact: 'MEDIUM',
        category: 'HABIT',
        actionUrl: '/habits',
      },
      {
        id: 3,
        type: 'LEARNING_MOMENTUM',
        title: 'Advance Spring Boot to 70%',
        description: 'Complete the Spring Security module to unlock backend integration.',
        impact: 'MEDIUM',
        category: 'LEARNING',
        actionUrl: '/learning',
      }
    ]
  };

  const profileSummary = {
    id: 1,
    name: 'Alex Chen',
    email: 'alex.chen@university.edu',
    createdAt: '2026-09-01T08:00:00Z',
    totalGoals: goals.length,
    totalTasks: tasks.length,
    totalHabits: habits.length,
    totalLearningItems: learningItems.length,
    totalNotes: notes.length,
  };

  return {
    user,
    goals,
    milestones,
    tasks,
    habits,
    learningItems,
    learningSessions,
    calendarEvents,
    notes,
    goalHealthOverview,
    analyticsDashboard,
    dailyFocus,
    consolidatedRecommendations,
    profileSummary,
  };
};
