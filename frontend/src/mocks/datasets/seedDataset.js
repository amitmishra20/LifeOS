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

  const goalProjects = createGoal({
    id: 'goal-projects',
    title: 'Build Portfolio Projects',
    category: 'CAREER',
    targetDate: 'Nov 10, 2026',
    status: 'ACTIVE',
    health: 'ON_TRACK',
    icon: 'code',
  });

  const goalInterview = createGoal({
    id: 'goal-interview',
    title: 'Prepare for Technical Interviews',
    category: 'CAREER',
    targetDate: 'Dec 1, 2026',
    status: 'ACTIVE',
    health: 'ON_TRACK',
    icon: 'education',
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

  const msPortfolio = createMilestone({
    id: 'ms-portfolio',
    goalId: goalProjects.id,
    title: 'Build Portfolio Projects',
    targetDate: 'Nov 10, 2026',
    status: 'ACTIVE',
    order: 1,
  });
  goalProjects.milestones = [msPortfolio];

  const msDsa = createMilestone({
    id: 'ms-dsa',
    goalId: goalInterview.id,
    title: 'Solve DSA Problems Consistently',
    targetDate: 'Dec 1, 2026',
    status: 'ACTIVE',
    order: 1,
  });
  goalInterview.milestones = [msDsa];

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
      isPrimary: false,
      whyReason: 'Master state management and component lifecycle for ShopSync.',
      actionLabel: 'Start Learning Session',
    }),
    createTask({
      id: 'task-2',
      goalId: goalProjects.id,
      milestoneId: msPortfolio.id,
      title: 'Build ShopSync UI Components',
      category: 'Project',
      time: '1:00 PM',
      duration: '45m',
      priority: 'HIGH',
      completed: false,
      isPrimary: true,
      whyReason: 'Implement responsive product catalog cards.',
      actionLabel: 'Open Component Studio',
    }),
    createTask({
      id: 'task-3',
      goalId: goalInterview.id,
      milestoneId: msDsa.id,
      title: 'Solve one DSA problem',
      category: 'Interview prep',
      time: '6:00 PM',
      duration: '45m',
      priority: 'MEDIUM',
      completed: false,
      whyReason: 'Build the problem-solving fluency needed for interviews.',
    }),
    createTask({
      id: 'task-4',
      goalId: goalInterview.id,
      milestoneId: msDsa.id,
      title: "Review yesterday's notes",
      category: 'Reflection',
      time: '8:30 PM',
      duration: '20m',
      priority: 'LOW',
      completed: false,
      whyReason: 'Turn yesterday\'s learning into durable understanding.',
    }),
  ];

  // 4. Habit Consistency Matrix
  const habits = [
    createHabit({
      id: 'habit-1',
      title: 'Coding practice',
      icon: 'code',
      history: [true, true, true, true, true, false, false],
    }),
    createHabit({
      id: 'habit-2',
      title: 'Reading',
      icon: 'education',
      history: [true, true, true, true, false, true, false],
    }),
    createHabit({
      id: 'habit-3',
      title: 'Exercise',
      icon: 'workout',
      history: [true, true, true, false, true, false, false],
    }),
    createHabit({
      id: 'habit-4',
      title: 'DSA practice',
      icon: 'code',
      history: [true, true, false, true, false, false, false],
    }),
  ];

  return {
    user: {
      id: 'user-amit',
      name: 'Amit',
      timeZone: 'America/New_York',
    },
    goals: [goalInternship, goalProjects, goalInterview],
    milestones: [msSkills, msShopSync, msInterview, msPortfolio, msDsa],
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
