import { createGoal } from '../factories/createGoal';
import { createMilestone } from '../factories/createMilestone';
import { createTask } from '../factories/createTask';
import { createHabit } from '../factories/createHabit';

/**
 * LifeOS Single Coherent Seed Dataset
 * Persona: CS Student (Amit) working toward a Software Internship.
 * Core narrative: Goal -> Milestone -> Task -> Action -> Progress.
 */
export const createSeedDataset = () => {
  // 1. Strategic Goals
  const goalInternship = createGoal({
    id: 'goal-internship',
    title: 'Land a Software Internship',
    category: 'CAREER',
    targetDate: 'Dec 15, 2026',
    status: 'ACTIVE',
    health: 'ON_TRACK',
    icon: 'code',
  });

  const goalJava = createGoal({
    id: 'goal-java',
    title: 'Complete Java Full Stack Specialization',
    category: 'GROWTH',
    targetDate: 'Nov 30, 2026',
    status: 'ACTIVE',
    health: 'AT_RISK',
    icon: 'education',
  });

  const goalHealth = createGoal({
    id: 'goal-health',
    title: 'Physical Health & Athletic Conditioning',
    category: 'HEALTH',
    targetDate: 'Ongoing',
    status: 'ACTIVE',
    health: 'ON_TRACK',
    icon: 'heart',
  });

  // 2. Strategic Milestones
  const msSkills = createMilestone({
    id: 'ms-skills',
    goalId: goalInternship.id,
    title: 'Build Core Frontend Skills',
    targetDate: 'Oct 15, 2026',
    status: 'ACTIVE',
    order: 1,
  });

  const msShopSync = createMilestone({
    id: 'ms-shopsync',
    goalId: goalInternship.id,
    title: 'Ship ShopSync Portfolio Project',
    targetDate: 'Nov 10, 2026',
    status: 'UPCOMING',
    order: 2,
  });

  const msInterview = createMilestone({
    id: 'ms-interview',
    goalId: goalInternship.id,
    title: 'Technical Interview Prep & Applications',
    targetDate: 'Dec 15, 2026',
    status: 'UPCOMING',
    order: 3,
  });

  goalInternship.milestones = [msSkills, msShopSync, msInterview];

  const msJavaSpring = createMilestone({
    id: 'ms-java-spring',
    goalId: goalJava.id,
    title: 'Spring Boot REST APIs & Microservices',
    targetDate: 'Nov 20, 2026',
    status: 'ACTIVE',
    order: 1,
  });
  goalJava.milestones = [msJavaSpring];

  const msHealthWeekly = createMilestone({
    id: 'ms-health-weekly',
    goalId: goalHealth.id,
    title: 'Weekly Strength & Cardio Consistency',
    targetDate: 'Ongoing',
    status: 'ACTIVE',
    order: 1,
  });
  goalHealth.milestones = [msHealthWeekly];

  // 3. Actionable Tasks (Genuinely linked to Milestones & Goals)
  const tasks = [
    createTask({
      id: 'task-1',
      goalId: goalInternship.id,
      milestoneId: msSkills.id,
      title: 'Complete React Fundamentals',
      category: 'Learning',
      time: '10:00 AM',
      duration: '90m',
      priority: 'HIGH',
      completed: false,
      isPrimary: true,
      whyReason: 'Master state management and component lifecycle for ShopSync.',
      actionLabel: 'Start Learning Session',
    }),
    createTask({
      id: 'task-2',
      goalId: goalInternship.id,
      milestoneId: msShopSync.id,
      title: 'Build ShopSync UI Components',
      category: 'Project',
      time: '1:00 PM',
      duration: '45m',
      priority: 'MEDIUM',
      completed: false,
      whyReason: 'Implement responsive product catalog cards.',
      actionLabel: 'Open Component Studio',
    }),
    createTask({
      id: 'task-3',
      goalId: goalHealth.id,
      milestoneId: msHealthWeekly.id,
      title: 'Gym & Strength Training',
      category: 'Health',
      time: '6:00 PM',
      duration: '60m',
      priority: 'MEDIUM',
      completed: false,
      whyReason: 'Upper body strength routine and recovery stretch.',
    }),
    createTask({
      id: 'task-4',
      goalId: goalJava.id,
      milestoneId: msJavaSpring.id,
      title: 'Review Java OOP Principles & Design Patterns',
      category: 'Learning',
      time: '8:30 PM',
      duration: '30m',
      priority: 'LOW',
      completed: true,
      whyReason: 'Strengthen backend concepts for internship interviews.',
    }),
  ];

  // 4. Habit Consistency Matrix
  const habits = [
    createHabit({
      id: 'habit-1',
      title: 'Morning Focus Session',
      icon: 'code',
      history: [true, true, true, true, true, false, false],
    }),
    createHabit({
      id: 'habit-2',
      title: 'Hydration & Nutrition',
      icon: 'water',
      history: [true, true, true, true, false, true, false],
    }),
    createHabit({
      id: 'habit-3',
      title: 'Daily Exercise / Movement',
      icon: 'workout',
      history: [true, true, true, true, true, false, false],
    }),
  ];

  return {
    user: {
      id: 'user-amit',
      name: 'Amit',
      timeZone: 'America/New_York',
    },
    goals: [goalInternship, goalJava, goalHealth],
    milestones: [msSkills, msShopSync, msInterview, msJavaSpring, msHealthWeekly],
    tasks,
    habits,
    guidance: {
      observation: 'Morning focus blocks show your highest task completion.',
      whyItMatters: 'You completed 80% of your programming milestones during morning blocks.',
      suggestion: 'Protect tomorrow morning for React Fundamentals and ShopSync architecture.',
      actionType: 'PROTECT_BLOCK',
      targetId: 'calendar-block-tomorrow',
      actionLabel: 'Protect Morning Block',
    },
  };
};
