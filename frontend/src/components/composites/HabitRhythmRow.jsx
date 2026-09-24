import React from 'react';
import './HabitRhythmRow.css';

/**
 * HabitRhythmRow Composite
 * Visual rhythm continuity representation using non-punitive dot fields.
 * Supports interactive fluid dot toggle.
 */
export const HabitRhythmRow = ({ habit, onToggleHabit }) => {
  if (!habit) return null;

  const { id, title, icon, history = [], summaryText } = habit;

  return (
    <div className="lifeos-habit-rhythm-row">
      <div className="lifeos-habit-rhythm-row__info">
        <div className="lifeos-habit-icon-pill" aria-hidden="true">
          {icon === 'code' && '⚡'}
          {icon === 'workout' && '🏃'}
          {icon === 'water' && '💧'}
          {icon === 'book' && '📖'}
          {icon === 'heart' && '❤️'}
          {!['code', 'workout', 'water', 'book', 'heart'].includes(icon) && '✦'}
        </div>
        <span className="lifeos-habit-title" title={title}>{title}</span>
      </div>

      <div className="lifeos-habit-rhythm-row__cadence">
        <div className="lifeos-habit-dot-field" role="group" aria-label={`7-day rhythm for ${title}`}>
          {history.map((completed, idx) => (
            <button
              key={`dot-${idx}`}
              type="button"
              className={`lifeos-rhythm-dot ${completed ? 'lifeos-rhythm-dot--completed' : ''}`}
              title={completed ? `Day ${idx + 1}: Done (Click to uncheck)` : `Day ${idx + 1}: Open (Click to mark done)`}
              onClick={() => onToggleHabit && onToggleHabit(id, idx)}
              aria-pressed={completed}
              aria-label={`Day ${idx + 1} for ${title}`}
            />
          ))}
        </div>
        <span className="lifeos-habit-summary">{summaryText}</span>
      </div>
    </div>
  );
};

export default HabitRhythmRow;
