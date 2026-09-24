import React from 'react';
import './JourneyWaypoint.css';

/**
 * JourneyWaypoint Composite
 * Represents a spatial checkpoint along the active Life Journey.
 * Supports interactive unfolding of constituent tasks along the topological branch.
 */
export const JourneyWaypoint = ({
  waypoint,
  isExpanded,
  onClick,
  onToggleTask,
}) => {
  if (!waypoint) return null;

  const { label, sublabel, status, type, isCurrent, tasks, completionRate } = waypoint;
  const hasTasks = tasks && tasks.length > 0;

  return (
    <div 
      className={`lifeos-journey-waypoint lifeos-journey-waypoint--${status} ${
        isCurrent ? 'lifeos-journey-waypoint--current' : ''
      } ${isExpanded ? 'lifeos-journey-waypoint--expanded' : ''}`}
      onClick={() => onClick && onClick(waypoint)}
      role="button"
      tabIndex={0}
      aria-expanded={isExpanded}
      aria-label={`Waypoint: ${label} (${sublabel || status})`}
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault();
          onClick && onClick(waypoint);
        }
      }}
    >
      <div className="lifeos-journey-waypoint__marker">
        {isCurrent && (
          <div className="lifeos-waypoint-current-pulse" aria-hidden="true" />
        )}

        {status === 'completed' ? (
          <div className="lifeos-waypoint-icon lifeos-waypoint-icon--completed" title="Completed checkpoint">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round">
              <polyline points="20 6 9 17 4 12" />
            </svg>
          </div>
        ) : type === 'destination' ? (
          <div className="lifeos-waypoint-icon lifeos-waypoint-icon--destination" title="Destination Summit">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
              <path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z" />
              <line x1="4" y1="22" x2="4" y2="15" stroke="currentColor" strokeWidth="2" />
            </svg>
          </div>
        ) : (
          <div className="lifeos-waypoint-dot" />
        )}
      </div>

      <div className="lifeos-journey-waypoint__label-block">
        <div className="lifeos-journey-waypoint__header-row">
          <span className="lifeos-journey-waypoint__title">{label}</span>
          {hasTasks && (
            <span className="lifeos-journey-waypoint__expand-hint">
              {isExpanded ? '▾' : '▸'} {tasks.length} {tasks.length === 1 ? 'task' : 'tasks'}
            </span>
          )}
        </div>
        {sublabel && (
          <span className="lifeos-journey-waypoint__subtitle">
            {sublabel} {completionRate > 0 && completionRate < 100 ? `· ${completionRate}%` : ''}
          </span>
        )}
      </div>

      {/* Spatial Branch: Unfolds connected tasks on click */}
      {isExpanded && hasTasks && (
        <div className="lifeos-waypoint-branch" role="list" onClick={(e) => e.stopPropagation()}>
          <div className="lifeos-waypoint-branch__stem" aria-hidden="true" />
          <div className="lifeos-waypoint-branch__tasks">
            {tasks.map((task) => (
              <div
                key={task.id}
                className={`lifeos-branch-task-node ${task.completed ? 'lifeos-branch-task-node--completed' : ''}`}
                role="listitem"
              >
                <button
                  type="button"
                  className={`lifeos-branch-task-check ${task.completed ? 'lifeos-branch-task-check--checked' : ''}`}
                  onClick={() => onToggleTask && onToggleTask(task.id)}
                  aria-label={`Toggle completion for ${task.title}`}
                  aria-pressed={task.completed}
                >
                  {task.completed ? '✓' : ''}
                </button>
                <div className="lifeos-branch-task-info">
                  <span className="lifeos-branch-task-title">{task.title}</span>
                  {task.duration && (
                    <span className="lifeos-branch-task-meta">{task.duration} · {task.priority}</span>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default JourneyWaypoint;
