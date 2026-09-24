/**
 * Dashboard View Model Builder
 * Source of Truth: LIFEOS — PHASE 3 PRODUCT SYSTEM + UI/UX MATURATION
 * 
 * Takes raw domain entities (user, goals, milestones, tasks, habits, guidance)
 * and strictly calculates:
 * 1. LifeOS Derived State (momentum, stateMode, rates)
 * 2. Section-specific ViewModels (Identity, LifeState, Focus, Journey, Rhythm, Guidance, Goals)
 * 
 * CRITICAL RULE: Pure data structures only. Zero UI functions or action handlers.
 */

export const buildDashboardViewModel = ({
  user,
  goals = [],
  milestones = [],
  tasks = [],
  habits = [],
  guidance = null,
} = {}) => {
  // 1. Relational Map Creation
  const goalsMap = new Map(goals.map((g) => [g.id, g]));
  const milestonesMap = new Map(milestones.map((m) => [m.id, m]));

  // 2. Metrics & Task Calculations
  const totalTasksCount = tasks.length;
  const completedTasks = tasks.filter((t) => t.completed);
  const completedTasksCount = completedTasks.length;

  const activeGoals = goals.filter((g) => g.status === 'ACTIVE');
  const activeGoalsCount = activeGoals.length;

  // Calculate habit consistency across all habits
  let totalHabitOpportunities = 0;
  let totalHabitCompletions = 0;
  habits.forEach((h) => {
    totalHabitOpportunities += h.history.length;
    totalHabitCompletions += h.history.filter(Boolean).length;
  });
  const habitsConsistencyRate =
    totalHabitOpportunities > 0
      ? Math.round((totalHabitCompletions / totalHabitOpportunities) * 100)
      : 0;

  // Momentum is only meaningful after the user has created activity.
  const taskExecutionRate =
    totalTasksCount > 0 ? (completedTasksCount / totalTasksCount) * 100 : 0;
  const goalPresenceFactor = activeGoalsCount > 0 ? 100 : 0;
  const momentumScore = Math.round(
    taskExecutionRate * 0.5 + habitsConsistencyRate * 0.3 + goalPresenceFactor * 0.2
  );

  // 3. Derived Lifecycle State Mode
  let stateMode = 'ACTIVE';
  if (totalTasksCount > 0 && completedTasksCount === totalTasksCount) {
    stateMode = 'ALL_COMPLETED';
  }

  // Time of Day Greeting
  const currentHour = new Date().getHours();
  let timeOfDayGreeting = 'Good morning';
  if (currentHour >= 12 && currentHour < 17) {
    timeOfDayGreeting = 'Good afternoon';
  } else if (currentHour >= 17) {
    timeOfDayGreeting = 'Good evening';
  }

  // 4. Life State Headline & Contextual Statement (Derived from State Mode)
  let headlineStatement = goals.length || tasks.length || habits.length ? 'BUILD WITH INTENTION' : 'YOUR LIFE STARTS HERE';
  let contextStatement = goals.length || tasks.length || habits.length
    ? 'Small, consistent actions build a life that feels like yours.'
    : 'Create one meaningful thing and let LifeOS grow around it.';

  if (stateMode === 'ALL_COMPLETED') {
    headlineStatement = 'TODAY IS COMPLETE';
    contextStatement = 'Everything planned for today is complete.';
  }

  // 5. FocusViewModel
  // Find primary task: explicit isPrimary or highest priority uncompleted
  let primaryTaskEntity = tasks.find((t) => t.isPrimary && !t.completed) ||
    tasks.find((t) => !t.completed && t.priority === 'HIGH') ||
    tasks.find((t) => !t.completed) ||
    tasks[0] ||
    null;

  let primaryFocus = null;
  if (primaryTaskEntity) {
    const parentMilestone = milestonesMap.get(primaryTaskEntity.milestoneId);
    const parentGoal = goalsMap.get(primaryTaskEntity.goalId);

    // Calculate milestone progress: how many tasks under this milestone are completed?
    const milestoneTasks = tasks.filter((t) => t.milestoneId === primaryTaskEntity.milestoneId);
    const milestoneCompletedCount = milestoneTasks.filter((t) => t.completed).length;
    const milestoneTotalCount = milestoneTasks.length;
    const milestoneRate =
      milestoneTotalCount > 0 ? Math.round((milestoneCompletedCount / milestoneTotalCount) * 100) : 0;

    primaryFocus = {
      task: {
        id: primaryTaskEntity.id,
        title: primaryTaskEntity.title,
        category: primaryTaskEntity.category,
        time: primaryTaskEntity.time,
        duration: primaryTaskEntity.duration,
        priority: primaryTaskEntity.priority,
        completed: primaryTaskEntity.completed,
      },
      milestone: parentMilestone
        ? {
            id: parentMilestone.id,
            title: parentMilestone.title,
            progressRate: milestoneRate,
            stepSummary: `${milestoneCompletedCount} of ${milestoneTotalCount} steps completed`,
          }
        : null,
      goal: parentGoal
        ? {
            id: parentGoal.id,
            title: parentGoal.title,
            category: parentGoal.category,
          }
        : null,
      whyReason: primaryTaskEntity.whyReason || 'Critical path to milestone delivery.',
      nextActionLabel: primaryTaskEntity.actionLabel || 'Begin Focus Session',
    };
  }

  // Supporting tasks: tasks other than primary
  const supportingTasks = tasks
    .filter((t) => (primaryTaskEntity ? t.id !== primaryTaskEntity.id : true))
    .map((t) => {
      const parentM = milestonesMap.get(t.milestoneId);
      const parentG = goalsMap.get(t.goalId);
      return {
        id: t.id,
        title: t.title,
        time: t.time,
        duration: t.duration,
        category: t.category,
        priority: t.priority,
        completed: t.completed,
        milestoneTitle: parentM ? parentM.title : null,
        goalTitle: parentG ? parentG.title : null,
      };
    });

  // 6. JourneyViewModel
  // Select primary strategic goal for the journey
  const journeyGoal = goals[0] || null;
  let waypoints = [];

  if (journeyGoal && journeyGoal.milestones) {
    // Waypoint 0: Current Position
    waypoints.push({
      id: 'current-pos',
      label: 'Current Position',
      sublabel: 'Active Cadence',
      type: 'position',
      status: completedTasksCount > 0 ? 'in-motion' : 'ready',
      isCurrent: true,
      completionRate: 100,
      tasks: [],
    });

    journeyGoal.milestones.forEach((m, idx) => {
      const mTasks = tasks.filter((t) => t.milestoneId === m.id);
      const mCompleted = mTasks.filter((t) => t.completed).length;
      const isFinished = m.status === 'COMPLETED' || (mTasks.length > 0 && mCompleted === mTasks.length);

      waypoints.push({
        id: m.id,
        label: m.title,
        sublabel: `Checkpoint ${idx + 1}`,
        type: 'milestone',
        status: isFinished ? 'completed' : m.status === 'ACTIVE' ? 'active' : 'upcoming',
        isCurrent: m.status === 'ACTIVE',
        completionRate: mTasks.length > 0 ? Math.round((mCompleted / mTasks.length) * 100) : 0,
        tasks: mTasks.map((t) => ({
          id: t.id,
          title: t.title,
          duration: t.duration,
          priority: t.priority,
          completed: t.completed,
        })),
      });
    });

    // Destination waypoint
    waypoints.push({
      id: `dest-${journeyGoal.id}`,
      label: journeyGoal.title,
      sublabel: 'Destination',
      type: 'destination',
      status: journeyGoal.status === 'COMPLETED' ? 'completed' : 'destination',
      isCurrent: false,
      completionRate: 0,
      tasks: [],
    });
  }

  // 7. RhythmViewModel
  const dayNames = ['M', 'T', 'W', 'T', 'F', 'S', 'S'];
  const currentDayIndex = ((new Date().getDay() + 6) % 7); // Monday = 0, Sunday = 6
  const weekDays = dayNames.map((label, idx) => ({
    key: `day-${idx}`,
    label,
    isToday: idx === currentDayIndex,
  }));

  const habitsList = habits.map((h) => ({
    id: h.id,
    title: h.title,
    icon: h.icon,
    history: h.history,
    completedDaysCount: h.completedDaysCount,
    totalDays: h.totalDays,
    consistencyRate: h.consistencyRate,
    summaryText: `${h.completedDaysCount} of ${h.totalDays} days completed`,
  }));

  // 8. GoalsViewModel
  const goalsSummary = goals.map((g) => {
    const gMilestones = g.milestones || [];
    const completedM = gMilestones.filter((m) => m.status === 'COMPLETED').length;
    const totalM = gMilestones.length;
    const progressRate = totalM > 0 ? Math.round((completedM / totalM) * 100) : 0;

    return {
      id: g.id,
      title: g.title,
      category: g.category,
      icon: g.icon,
      progressRate,
      milestonesSummary: `${completedM} of ${totalM} milestones`,
      milestoneChain: gMilestones.map((m) => ({
        id: m.id,
        title: m.title,
        completed: m.status === 'COMPLETED',
        active: m.status === 'ACTIVE',
      })),
      healthStatus: g.health || 'ON_TRACK',
      isAtRisk: g.health === 'AT_RISK',
    };
  });

  return {
    identity: {
      id: user?.id || null,
      name: user?.name || null,
      timeZone: user?.timeZone || Intl.DateTimeFormat().resolvedOptions().timeZone,
    },
    lifeState: {
      stateMode,
      headlineStatement,
      contextStatement,
      timeOfDayGreeting,
      momentumScore,
      activeGoalsCount,
      completedTasksCount,
      totalTasksCount,
      habitsConsistencyRate,
      dateString: new Date().toLocaleDateString('en-US', {
        weekday: 'long',
        month: 'short',
        day: 'numeric',
      }),
    },
    focus: {
      hasTasks: totalTasksCount > 0,
      primaryFocus,
      supportingTasks,
    },
    journey: {
      hasJourney: waypoints.length > 0,
      destinationGoal: journeyGoal ? { id: journeyGoal.id, title: journeyGoal.title } : null,
      waypoints,
    },
    rhythm: {
      weekDays,
      habits: habitsList,
      hasHabits: habitsList.length > 0,
    },
    guidance,

    goals: {
      goalsList: goalsSummary,
      hasGoals: goalsSummary.length > 0,
    },
  };
};
