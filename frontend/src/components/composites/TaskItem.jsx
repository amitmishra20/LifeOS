import React from 'react';
import './TaskItem.css';

/**
 * TaskItem Composite
 * Represents a state-aware task row with completion toggle, time, category, and milestone linkage.
 */
export const TaskItem = ({
  task,
  onToggle,
  isReceded = false,
}) => {
  if (!task) return null;

  return (
    <div
      role="button"
      tabIndex={0}
      aria-label={`Toggle task: ${task.title}`}
      aria-pressed={task.completed}
      className={`lifeos-task-item ${task.completed ? 'lifeos-task-item--completed' : ''} ${
        isReceded ? 'lifeos-task-item--receded' : ''
      }`}
      onClick={() => onToggle && onToggle(task.id)}
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault();
          onToggle && onToggle(task.id);
        }
      }}
    >
      <div className="lifeos-task-item__checkbox-wrap">
        <div className={`lifeos-task-item__checkbox ${task.completed ? 'lifeos-task-item__checkbox--checked' : ''}`}>
          {task.completed && (
            <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round">
              <polyline points="20 6 9 17 4 12" />
            </svg>
          )}
        </div>
      </div>

      <div className="lifeos-task-item__body">
        <span className="lifeos-task-item__title">{task.title}</span>
        
        <div className="lifeos-task-item__meta">
          {task.time && <span className="lifeos-task-item__time">{task.time}</span>}
          {task.category && (
            <>
              <span className="lifeos-task-item__dot">·</span>
              <span className="lifeos-task-item__category">{task.category}</span>
            </>
          )}
          {task.milestoneTitle && (
            <>
              <span className="lifeos-task-item__dot">·</span>
              <span className="lifeos-task-item__milestone" title={task.milestoneTitle}>
                {task.milestoneTitle}
              </span>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default TaskItem;
