/**
 * Habit Factory
 * Represents recurring rhythms and daily consistency.
 */
let habitCounter = 1;

export const createHabit = ({
  id,
  title,
  icon = 'book',
  history = [true, true, true, true, true, false, false], // Last 7 days
} = {}) => {
  const habitId = id || `habit-${habitCounter++}`;
  const completedDaysCount = history.filter(Boolean).length;
  return {
    id: habitId,
    title,
    icon,
    history,
    completedDaysCount,
    totalDays: history.length,
    consistencyRate: Math.round((completedDaysCount / history.length) * 100),
  };
};
