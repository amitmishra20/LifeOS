import React from 'react';
import HabitRhythmRow from '../composites/HabitRhythmRow';
import './RhythmSection.css';

/**
 * RhythmSection Experience Component
 * Visual rhythm continuity representation using non-punitive dot fields.
 */
export const RhythmSection = ({ rhythmData, onToggleHabit }) => {
  if (!rhythmData || !rhythmData.hasHabits) {
    return (
      <div className="lifeos-rhythm-section">
        <div className="lifeos-rhythm-card">
          <div className="lifeos-card-header">
            <h3 className="lifeos-card-title">This Week's Rhythm</h3>
            <p className="lifeos-card-subtitle">Consistency builds momentum</p>
          </div>
          <div className="lifeos-rhythm-empty">
            <p>No active rhythms tracked yet.</p>
          </div>
        </div>
      </div>
    );
  }

  const { weekDays, habits } = rhythmData;

  return (
    <div className="lifeos-rhythm-section">
      <div className="lifeos-rhythm-card">
        <div className="lifeos-card-header">
          <h3 className="lifeos-card-title">This Week's Rhythm</h3>
          <p className="lifeos-card-subtitle">Consistency builds momentum</p>
        </div>

        {/* Weekdays indicator pills */}
        <div className="lifeos-rhythm-days-header" aria-label="Weekly Active Days">
          {weekDays.map((d) => (
            <div
              key={d.key}
              className={`lifeos-day-pill ${d.isToday ? 'lifeos-day-pill--today' : ''}`}
              title={d.isToday ? "Today" : d.label}
            >
              <span>{d.label}</span>
            </div>
          ))}
        </div>

        {/* Habit Rhythm Rows */}
        <div className="lifeos-rhythm-habits-list" role="list">
          {habits.map((h) => (
            <div key={h.id} role="listitem">
              <HabitRhythmRow habit={h} onToggleHabit={onToggleHabit} />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default RhythmSection;
