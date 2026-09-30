import React from 'react';
import './HabitRhythmRow.css';

/**
 * HabitRhythmRow Composite
 * Visual rhythm continuity representation using non-punitive structured dot fields.
 * Supports interactive fluid dot toggle with scheduled, completed, missed, and rest states.
 */
export const HabitRhythmRow = ({ habit, onToggleHabit }) => {
  if (!habit) return null;

  const {
    id,
    title,
    icon,
    frequencyType,
    currentStreak,
    consistencyRate,
    weeklyTargetProgress,
    weeklyTargetRemaining,
    targetPerWeek,
    history = [],
    summaryText,
  } = habit;

  const formatSummary = () => {
    if (summaryText) return summaryText;
    if (frequencyType === 'WEEKLY_TARGET') {
      const progress = weeklyTargetProgress !== undefined ? weeklyTargetProgress : 0;
      const target = targetPerWeek || 7;
      return `${progress} of ${target} this week`;
    }
    const streak = currentStreak !== undefined && currentStreak !== null ? `${currentStreak}d streak` : null;
    const rate = consistencyRate !== undefined ? `${consistencyRate}%` : null;
    if (streak && rate) return `${streak} · ${rate}`;
    return rate || streak || '';
  };

  const getIconGlyph = (iconType) => {
    switch (iconType) {
      case 'code': return '⚡';
      case 'workout': return '🏃';
      case 'water': return '💧';
      case 'book': return '📖';
      case 'heart': return '❤️';
      default: return '✦';
    }
  };

  return (
    <div className="lifeos-habit-rhythm-row">
      <div className="lifeos-habit-rhythm-row__info">
        <div className="lifeos-habit-icon-pill" aria-hidden="true">
          {getIconGlyph(icon)}
        </div>
        <div className="lifeos-habit-rhythm-row__meta">
          <span className="lifeos-habit-title" title={title}>{title}</span>
          {habit.goalTitle && (
            <span className="lifeos-habit-goal-tag" title={`Goal: ${habit.goalTitle}`}>
              {habit.goalTitle}
            </span>
          )}
        </div>
      </div>

      <div className="lifeos-habit-rhythm-row__cadence">
        <div className="lifeos-habit-dot-field" role="group" aria-label={`7-day rhythm for ${title}`}>
          {history.map((dayItem, idx) => {
            const isObj = typeof dayItem === 'object' && dayItem !== null;
            const completed = isObj ? dayItem.isCompleted : Boolean(dayItem);
            const scheduled = isObj ? dayItem.isScheduled : true;
            const paused = isObj ? dayItem.isPaused : false;
            const isToday = isObj ? dayItem.isToday : idx === history.length - 1;
            const dateStr = isObj ? dayItem.date : null;
            const dayLabel = isObj ? dayItem.dayOfWeek : `Day ${idx + 1}`;

            let statusClass = 'lifeos-rhythm-dot--open';
            let titleText = `${dayLabel}: Open (Click to mark completed)`;

            if (paused) {
              statusClass = 'lifeos-rhythm-dot--paused';
              titleText = `${dayLabel}: Paused`;
            } else if (completed) {
              statusClass = 'lifeos-rhythm-dot--completed';
              titleText = `${dayLabel}: Completed (Click to uncheck)`;
            } else if (!scheduled) {
              statusClass = 'lifeos-rhythm-dot--rest';
              titleText = `${dayLabel}: Flexible / Rest day`;
            } else {
              statusClass = 'lifeos-rhythm-dot--open';
              titleText = isToday ? `Today (${dayLabel}): Open (Click to mark done)` : `${dayLabel}: Missed`;
            }

            return (
              <button
                key={`dot-${dateStr || idx}`}
                type="button"
                className={`lifeos-rhythm-dot ${statusClass} ${isToday ? 'lifeos-rhythm-dot--today' : ''}`}
                title={titleText}
                onClick={() => onToggleHabit && onToggleHabit(id, dateStr || idx)}
                aria-pressed={completed}
                aria-label={`${title}, ${dayLabel}: ${completed ? 'Completed' : (scheduled ? 'Scheduled' : 'Rest day')}`}
                disabled={habit.status === 'PAUSED' || habit.status === 'ARCHIVED'}
              >
                <span className="lifeos-rhythm-dot__inner" />
                <span className="lifeos-rhythm-dot__label" aria-hidden="true">
                  {isObj && dayItem.dayOfWeek ? dayItem.dayOfWeek.slice(0, 1) : ''}
                </span>
              </button>
            );
          })}
        </div>
        <span className="lifeos-habit-summary">{formatSummary()}</span>
      </div>
    </div>
  );
};

export default HabitRhythmRow;
