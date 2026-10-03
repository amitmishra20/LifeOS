import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import recommendationService from '../services/recommendationService';
import Button from '../components/ui/Button';
import LoadingState from '../components/ui/LoadingState';
import './RecommendationsPage.css';

export const RecommendationsPage = () => {
  const navigate = useNavigate();

  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchRecommendations = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await recommendationService.getRecommendations();
      setData(res);
    } catch (err) {
      setError(err.message || 'Failed to load recommendations.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchRecommendations();
  }, [fetchRecommendations]);

  const dateHeading = data?.generatedDate
    ? new Date(data.generatedDate + 'T00:00:00').toLocaleDateString('en-US', {
        weekday: 'long',
        month: 'short',
        day: 'numeric',
      })
    : new Date().toLocaleDateString('en-US', {
        weekday: 'long',
        month: 'short',
        day: 'numeric',
      });

  if (isLoading && !data) {
    return <LoadingState message="Synthesizing personalized system observations..." />;
  }

  const dailyFocusTasks = data?.dailyFocusTasks || [];
  const strategicAlerts = data?.strategicAlerts || [];
  const habitNudges = data?.habitNudges || [];
  const learningFocus = data?.learningFocus || [];

  const totalCount = data?.totalRecommendationsCount || 0;
  const isEmpty = totalCount === 0;

  return (
    <div className="lifeos-recommendations-page">
      {/* Editorial Header */}
      <header className="lifeos-rec-header">
        <div className="lifeos-rec-header__main">
          <div className="lifeos-rec-date-badge">
            <span className="lifeos-rec-date-dot" />
            <span className="lifeos-rec-date-text">{dateHeading}</span>
          </div>
          <h1 className="lifeos-rec-title">Observations & Synthesis</h1>
          <p className="lifeos-rec-subline">
            Deterministic, contextual observations synthesized from your goals, habits, learning, and daily execution.
          </p>
        </div>

        <div className="lifeos-rec-header__actions">
          <Button variant="ghost" onClick={() => navigate('/focus')}>
            Today's Focus →
          </Button>
          <Button variant="secondary" onClick={fetchRecommendations}>
            Refresh
          </Button>
        </div>
      </header>

      {/* Error state */}
      {error && (
        <div className="lifeos-rec-error" role="alert">
          <p>{error}</p>
          <Button variant="ghost" onClick={fetchRecommendations}>
            Retry
          </Button>
        </div>
      )}

      {/* Equilibrium State (When 0 recommendations exist) */}
      {!error && isEmpty && (
        <div className="lifeos-rec-equilibrium">
          <div className="lifeos-rec-equilibrium__mark" aria-hidden="true">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
              <path d="M9 12l2 2 4-4" />
            </svg>
          </div>
          <span className="lifeos-kicker">SYSTEM EQUILIBRIUM</span>
          <h2 className="lifeos-rec-equilibrium__title">
            All systems are <em>in balance.</em>
          </h2>
          <p className="lifeos-rec-equilibrium__desc">
            No critical pace deficits, stalled learning tracks, or habit regressions detected. Your trajectory remains steady and aligned with your destinations.
          </p>
          <div className="lifeos-rec-equilibrium__actions">
            <Button variant="primary" onClick={() => navigate('/focus')}>
              Continue Daily Focus →
            </Button>
            <Button variant="ghost" onClick={() => navigate('/goals')}>
              View Life Map
            </Button>
          </div>
        </div>
      )}

      {/* Categorized Recommendations Stream */}
      {!error && !isEmpty && (
        <div className="lifeos-rec-stream">
          {/* Strategic Risk Alerts */}
          {strategicAlerts.length > 0 && (
            <section className="lifeos-rec-group" aria-labelledby="strategic-heading">
              <div className="lifeos-rec-group__header">
                <div className="lifeos-rec-group__title-wrap">
                  <span className="lifeos-rec-group__num">01</span>
                  <span className="lifeos-rec-group__rule" />
                  <span className="lifeos-rec-group__kicker lifeos-rec-group__kicker--alert">STRATEGIC TRAJECTORY</span>
                </div>
                <span className="lifeos-rec-count-badge">
                  {strategicAlerts.length} {strategicAlerts.length === 1 ? 'observation' : 'observations'}
                </span>
              </div>

              <div className="lifeos-rec-rows">
                {strategicAlerts.map((alert) => (
                  <article key={alert.id} className="lifeos-rec-row lifeos-rec-row--strategic">
                    <div className="lifeos-rec-row__main">
                      <div className="lifeos-rec-row__meta">
                        <span className="lifeos-rec-tag lifeos-rec-tag--alert">Goal Pace Deficit</span>
                        <span className="lifeos-rec-subtitle">{alert.subtitle}</span>
                      </div>
                      <h3 className="lifeos-rec-row__title">{alert.title}</h3>
                      <div className="lifeos-rec-row__why">
                        <span className="lifeos-rec-why-label">Observation:</span> {alert.primaryReason}
                      </div>

                      {alert.reasons && alert.reasons.length > 1 && (
                        <ul className="lifeos-rec-reasons-list">
                          {alert.reasons.slice(1).map((r, i) => (
                            <li key={i}>{r}</li>
                          ))}
                        </ul>
                      )}
                    </div>

                    <div className="lifeos-rec-row__action">
                      <Button
                        variant="secondary"
                        size="sm"
                        onClick={() => navigate(alert.actionUrl)}
                      >
                        Inspect Goal →
                      </Button>
                    </div>
                  </article>
                ))}
              </div>
            </section>
          )}

          {/* Daily Focus Tasks */}
          {dailyFocusTasks.length > 0 && (
            <section className="lifeos-rec-group" aria-labelledby="focus-heading">
              <div className="lifeos-rec-group__header">
                <div className="lifeos-rec-group__title-wrap">
                  <span className="lifeos-rec-group__num">02</span>
                  <span className="lifeos-rec-group__rule" />
                  <span className="lifeos-rec-group__kicker">EXECUTION LEVERAGE</span>
                </div>
                <span className="lifeos-rec-count-badge">
                  {dailyFocusTasks.length} {dailyFocusTasks.length === 1 ? 'action' : 'actions'}
                </span>
              </div>

              <div className="lifeos-rec-rows">
                {dailyFocusTasks.map((item) => (
                  <article key={item.task.id} className="lifeos-rec-row lifeos-rec-row--focus">
                    <div className="lifeos-rec-row__main">
                      <div className="lifeos-rec-row__meta">
                        <span className="lifeos-rec-tag">High Leverage</span>
                        <span className="lifeos-rec-subtitle">
                          {item.task.goalTitle ? `🎯 ${item.task.goalTitle}` : 'Action'}
                          {item.task.dueDate ? ` · Due ${item.task.dueDate}` : ''}
                        </span>
                      </div>
                      <h3 className="lifeos-rec-row__title">{item.task.title}</h3>
                      <div className="lifeos-rec-row__why">
                        <span className="lifeos-rec-why-label">Observation:</span> {item.primaryReason}
                      </div>
                    </div>

                    <div className="lifeos-rec-row__action">
                      <Button
                        variant="primary"
                        size="sm"
                        onClick={() => navigate('/focus')}
                      >
                        Focus Now →
                      </Button>
                    </div>
                  </article>
                ))}
              </div>
            </section>
          )}

          {/* Habit Nudges */}
          {habitNudges.length > 0 && (
            <section className="lifeos-rec-group" aria-labelledby="habits-heading">
              <div className="lifeos-rec-group__header">
                <div className="lifeos-rec-group__title-wrap">
                  <span className="lifeos-rec-group__num">03</span>
                  <span className="lifeos-rec-group__rule" />
                  <span className="lifeos-rec-group__kicker lifeos-rec-group__kicker--warm">RHYTHM OBSERVATION</span>
                </div>
                <span className="lifeos-rec-count-badge">
                  {habitNudges.length} {habitNudges.length === 1 ? 'practice' : 'practices'}
                </span>
              </div>

              <div className="lifeos-rec-rows">
                {habitNudges.map((habit) => (
                  <article key={habit.id} className="lifeos-rec-row lifeos-rec-row--habit">
                    <div className="lifeos-rec-row__main">
                      <div className="lifeos-rec-row__meta">
                        <span className="lifeos-rec-tag lifeos-rec-tag--warm">Rhythm Drift</span>
                        <span className="lifeos-rec-subtitle">{habit.subtitle}</span>
                      </div>
                      <h3 className="lifeos-rec-row__title">{habit.title}</h3>
                      <div className="lifeos-rec-row__why">
                        <span className="lifeos-rec-why-label">Observation:</span> {habit.primaryReason}
                      </div>

                      {habit.reasons && habit.reasons.length > 1 && (
                        <ul className="lifeos-rec-reasons-list">
                          {habit.reasons.slice(1).map((r, i) => (
                            <li key={i}>{r}</li>
                          ))}
                        </ul>
                      )}
                    </div>

                    <div className="lifeos-rec-row__action">
                      <Button
                        variant="secondary"
                        size="sm"
                        onClick={() => navigate(habit.actionUrl)}
                      >
                        Log Practice →
                      </Button>
                    </div>
                  </article>
                ))}
              </div>
            </section>
          )}

          {/* Learning Focus */}
          {learningFocus.length > 0 && (
            <section className="lifeos-rec-group" aria-labelledby="learning-heading">
              <div className="lifeos-rec-group__header">
                <div className="lifeos-rec-group__title-wrap">
                  <span className="lifeos-rec-group__num">04</span>
                  <span className="lifeos-rec-group__rule" />
                  <span className="lifeos-rec-group__kicker">GROWTH CONTINUITY</span>
                </div>
                <span className="lifeos-rec-count-badge">
                  {learningFocus.length} {learningFocus.length === 1 ? 'subject' : 'subjects'}
                </span>
              </div>

              <div className="lifeos-rec-rows">
                {learningFocus.map((learn) => (
                  <article key={learn.id} className="lifeos-rec-row lifeos-rec-row--learning">
                    <div className="lifeos-rec-row__main">
                      <div className="lifeos-rec-row__meta">
                        <span className="lifeos-rec-tag">Stalled Subject</span>
                        <span className="lifeos-rec-subtitle">{learn.subtitle}</span>
                      </div>
                      <h3 className="lifeos-rec-row__title">{learn.title}</h3>
                      <div className="lifeos-rec-row__why">
                        <span className="lifeos-rec-why-label">Observation:</span> {learn.primaryReason}
                      </div>

                      {learn.reasons && learn.reasons.length > 1 && (
                        <ul className="lifeos-rec-reasons-list">
                          {learn.reasons.slice(1).map((r, i) => (
                            <li key={i}>{r}</li>
                          ))}
                        </ul>
                      )}
                    </div>

                    <div className="lifeos-rec-row__action">
                      <Button
                        variant="secondary"
                        size="sm"
                        onClick={() => navigate(learn.actionUrl)}
                      >
                        Record Session →
                      </Button>
                    </div>
                  </article>
                ))}
              </div>
            </section>
          )}
        </div>
      )}
    </div>
  );
};

export default RecommendationsPage;
