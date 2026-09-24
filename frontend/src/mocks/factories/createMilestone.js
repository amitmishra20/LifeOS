/**
 * Milestone Factory
 * Represents a concrete checkpoint along a Goal's trajectory.
 */
let milestoneCounter = 1;

export const createMilestone = ({
  id,
  goalId,
  title,
  targetDate = '2026-10-31',
  status = 'ACTIVE',
  order = 1,
} = {}) => {
  const milestoneId = id || `milestone-${milestoneCounter++}`;
  return {
    id: milestoneId,
    goalId,
    title,
    targetDate,
    status, // COMPLETED | ACTIVE | UPCOMING
    order,
  };
};
