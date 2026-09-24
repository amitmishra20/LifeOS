/**
 * Task Factory
 * Represents an actionable unit of work linked to a Milestone and Goal.
 */
let taskCounter = 1;

export const createTask = ({
  id,
  goalId,
  milestoneId,
  title,
  category = 'Focus',
  time = '10:00 AM',
  duration = '60m',
  priority = 'MEDIUM', // HIGH | MEDIUM | LOW
  completed = false,
  whyReason = '',
  actionLabel = 'Start Action',
  isPrimary = false,
} = {}) => {
  const taskId = id || `task-${taskCounter++}`;
  return {
    id: taskId,
    goalId,
    milestoneId,
    title,
    category,
    time,
    duration,
    priority,
    completed,
    whyReason,
    actionLabel,
    isPrimary,
  };
};
