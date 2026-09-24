/**
 * Goal Factory
 * Represents a high-level strategic life destination.
 */
let goalCounter = 1;

export const createGoal = ({
  id,
  title,
  category = 'CAREER',
  targetDate = '2026-12-31',
  status = 'ACTIVE',
  health = 'ON_TRACK',
  milestones = [],
  description = '',
  icon = 'code',
} = {}) => {
  const goalId = id || `goal-${goalCounter++}`;
  return {
    id: goalId,
    title,
    category,
    targetDate,
    status, // ACTIVE | COMPLETED | PAUSED
    health, // ON_TRACK | AT_RISK | CRITICAL
    milestones,
    description,
    icon,
  };
};
