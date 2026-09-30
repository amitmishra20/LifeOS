import React from 'react';
import Drawer from '../ui/Drawer';
import Button from '../ui/Button';
import LoadingState from '../ui/LoadingState';
import './LearningSessionsDrawer.css';

export const LearningSessionsDrawer = ({
  isOpen,
  onClose,
  learningItem,
  sessions = [],
  isLoading = false,
  onLogSession,
  onDeleteSession,
}) => {
  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    try {
      const parts = dateStr.split('-');
      if (parts.length === 3) {
        const d = new Date(Number(parts[0]), Number(parts[1]) - 1, Number(parts[2]));
        return d.toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' });
      }
      return dateStr;
    } catch {
      return dateStr;
    }
  };

  return (
    <Drawer
      isOpen={isOpen}
      onClose={onClose}
      title={learningItem ? `Practice History — ${learningItem.title}` : 'Practice History'}
      position="right"
      size="md"
    >
      <div className="lifeos-sessions-drawer">
        {learningItem && (
          <div className="lifeos-sessions-drawer__summary">
            <div className="lifeos-sessions-drawer__summary-info">
              <span className="lifeos-sessions-drawer__summary-title">
                {learningItem.totalHoursLearned || 0} Hours Invested
              </span>
              <span className="lifeos-sessions-drawer__summary-stats">
                {sessions.length} total recorded sessions
              </span>
            </div>
            {learningItem.status !== 'ARCHIVED' && (
              <Button
                variant="secondary"
                size="sm"
                onClick={() => {
                  onLogSession(learningItem);
                }}
              >
                + Log Session
              </Button>
            )}
          </div>
        )}

        {isLoading ? (
          <LoadingState message="Loading practice sessions..." />
        ) : sessions.length === 0 ? (
          <div className="lifeos-sessions-drawer__empty">
            <p>No practice sessions logged yet for this subject.</p>
            {learningItem && learningItem.status !== 'ARCHIVED' && (
              <div style={{ marginTop: '16px' }}>
                <Button variant="primary" size="sm" onClick={() => onLogSession(learningItem)}>
                  Log First Session
                </Button>
              </div>
            )}
          </div>
        ) : (
          <div className="lifeos-sessions-drawer__list">
            {sessions.map((session) => (
              <article key={session.id} className="lifeos-session-card">
                <div className="lifeos-session-card__header">
                  <div className="lifeos-session-card__meta">
                    <span className="lifeos-session-card__date">
                      {formatDate(session.sessionDate)}
                    </span>
                    <span className="lifeos-session-card__duration">
                      {session.durationMinutes} min
                    </span>
                  </div>
                  <button
                    type="button"
                    className="lifeos-session-card__delete-btn"
                    title="Delete session"
                    onClick={() => onDeleteSession(session.id)}
                    aria-label={`Delete session ${session.topic}`}
                  >
                    Delete
                  </button>
                </div>

                <h3 className="lifeos-session-card__topic">{session.topic}</h3>

                {session.notes && (
                  <p className="lifeos-session-card__notes">{session.notes}</p>
                )}
              </article>
            ))}
          </div>
        )}
      </div>
    </Drawer>
  );
};

export default LearningSessionsDrawer;
