import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import recommendationService from '../services/recommendationService';
import Button from '../components/ui/Button';
import EmptyState from '../components/ui/EmptyState';
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
    return <LoadingState message="Synthesizing personalized system recommendations..." />;
  }

  const dailyFocusTasks = data?.dailyFocusTasks || [];
  const strategicAlerts = data?.strategicAlerts || [];
  const habitNudges = data?.habitNudges || [];
  const learningFocus = data?.learningFocus || [];

  const totalCount = data?.totalRecommendationsCount || 0;
  const isEmpty = totalCount === 0;

  return (
    <div className="lifeos-recommendations-page">
      {/* Header */}
      <header className="lifeos-recommendations-header">
        <div className="lifeos-recommendations-header__main">
          <span className="lifeos-recommendations-date">{dateHeading}</span>
          <h1 className="lifeos-recommendations-title">Recommendations</h1>
          <p className="lifeos-recommendations-subline">
            Deterministic, explainable guidance across your goals, habits, learning, and daily focus.
          </p>
        </div>

        <div className="lifeos-recommendations-header__actions">
          <Button variant="ghost" onClick={() => navigate('/focus')}>
            Today&apos;s Focus →
          </Button>
          <Button variant="primary" onClick={fetchRecommendations}>
            Refresh Guidance
          </Button>
        </div>
      </header>

      {/* Error state */}
      {error && (
        <div className="lifeos-recommendations-error" role="alert">
          <p>{error}</p>
          <Button variant="ghost" onClick={fetchRecommendations}>
            Retry
          </Button>
        </div>
      )}

      {/* Empty State */}
      {!error && isEmpty && (
        <EmptyState
          title="All systems are performing on track"
          message="No critical alerts, stalled learning subjects, or habit deficits detected today. Continue executing with calm focus."
          actionLabel="View Life Map"
          onAction={() => navigate('/goals')}
        />
      )}

      {/* Categorized Recommendations Grid */}
      {!error && !isEmpty && (
        <div className="lifeos-recommendations-grid">
          {/* Strategic Risk Alerts */}
          {strategicAlerts.length > 0 && (
            <section className="lifeos-rec-section" aria-labelledby="strategic-heading">
              <div className="lifeos-rec-section__header">
                <div className="lifeos-rec-section__title-wrap">
                  <span className="lifeos-kicker lifeos-kicker--alert">STRATEGIC RISK</span>
                  <h2 id="strategic-heading" className="lifeos-rec-section__title">
                    Goals Needing Realignment
                  </h2>
                </div>
                <span className="lifeos-rec-section__badge lifeos-rec-badge--alert">
                  {strategicAlerts.length} {strategicAlerts.length === 1 ? 'alert' : 'alerts'}
                </span>
              </div>

              <div className="lifeos-rec-card-list">
                {strategicAlerts.map((alert) => (
                  <div key={alert.id} className="lifeos-rec-card lifeos-rec-card--strategic">
                    <div className="lifeos-rec-card__top">
                      <div className="lifeos-rec-card__info">
                        <span className="lifeos-rec-card__type">Goal Risk</span>
                        <h3 className="lifeos-rec-card__title">{alert.title}</h3>
                        <span className="lifeos-rec-card__subtitle">{alert.subtitle}</span>
                      </div>
                      <Button
                        variant="ghost"
                        size="small"
                        onClick={() => navigate(alert.actionUrl)}
                      >
                        Inspect Goal →
                      </Button>
                    </div>

                    <div className="lifeos-rec-card__explanation">
                      <span className="lifeos-rec-why-tag">Why:</span> {alert.primaryReason}
                    </div>

                    {alert.reasons && alert.reasons.length > 1 && (
                      <ul className="lifeos-rec-card__reasons-list">
                        {alert.reasons.slice(1).map((r, i) => (
                          <li key={i}>{r}</li>
                        ))}
                      </ul>
                    )}
                  </div>
                ))}
              </div>
            </section>
          )}

          {/* Daily Focus Tasks */}
          {dailyFocusTasks.length > 0 && (
            <section className="lifeos-rec-section" aria-labelledby="focus-heading">
              <div className="lifeos-rec-section__header">
                <div className="lifeos-rec-section__title-wrap">
                  <span className="lifeos-kicker">DAILY EXECUTION</span>
                  <h2 id="focus-heading" className="lifeos-rec-section__title">
                    Top Priority Tasks
                  </h2>
                </div>
                <span className="lifeos-rec-section__badge">
                  {dailyFocusTasks.length} {dailyFocusTasks.length === 1 ? 'task' : 'tasks'}
                </span>
              </div>

              <div className="lifeos-rec-card-list">
                {dailyFocusTasks.map((item) => (
                  <div key={item.task.id} className="lifeos-rec-card lifeos-rec-card--focus">
                    <div className="lifeos-rec-card__top">
                      <div className="lifeos-rec-card__info">
                        <span className="lifeos-rec-card__type">Task Focus · Score {item.totalScore}</span>
                        <h3 className="lifeos-rec-card__title">{item.task.title}</h3>
                        <span className="lifeos-rec-card__subtitle">
                          {item.task.goalTitle ? `Goal: ${item.task.goalTitle}` : 'Personal Action'}
                          {item.task.dueDate ? ` · Due ${item.task.dueDate}` : ''}
                        </span>
                      </div>
                      <Button
                        variant="ghost"
                        size="small"
                        onClick={() => navigate('/focus')}
                      >
                        Go to Focus →
                      </Button>
                    </div>

                    <div className="lifeos-rec-card__explanation">
                      <span className="lifeos-rec-why-tag">Why:</span> {item.primaryReason}
                    </div>
                  </div>
                ))}
              </div>
            </section>
          )}

          {/* Habit Nudges */}
          {habitNudges.length > 0 && (
            <section className="lifeos-rec-section" aria-labelledby="habits-heading">
              <div className="lifeos-rec-section__header">
                <div className="lifeos-rec-section__title-wrap">
                  <span className="lifeos-kicker lifeos-kicker--warning">RHYTHM NUDGES</span>
                  <h2 id="habits-heading" className="lifeos-rec-section__title">
                    Habits Requiring Consistency
                  </h2>
                </div>
                <span className="lifeos-rec-section__badge lifeos-rec-badge--warning">
                  {habitNudges.length} {habitNudges.length === 1 ? 'nudge' : 'nudges'}
                </span>
              </div>

              <div className="lifeos-rec-card-list">
                {habitNudges.map((habit) => (
                  <div key={habit.id} className="lifeos-rec-card lifeos-rec-card--habit">
                    <div className="lifeos-rec-card__top">
                      <div className="lifeos-rec-card__info">
                        <span className="lifeos-rec-card__type">Habit Consistency</span>
                        <h3 className="lifeos-rec-card__title">{habit.title}</h3>
                        <span className="lifeos-rec-card__subtitle">{habit.subtitle}</span>
                      </div>
                      <Button
                        variant="ghost"
                        size="small"
                        onClick={() => navigate(habit.actionUrl)}
                      >
                        Log Habit →
                      </Button>
                    </div>

                    <div className="lifeos-rec-card__explanation">
                      <span className="lifeos-rec-why-tag">Why:</span> {habit.primaryReason}
                    </div>

                    {habit.reasons && habit.reasons.length > 1 && (
                      <ul className="lifeos-rec-card__reasons-list">
                        {habit.reasons.slice(1).map((r, i) => (
                          <li key={i}>{r}</li>
                        ))}
                      </ul>
                    )}
                  </div>
                ))}
              </div>
            </section>
          )}

          {/* Learning Focus */}
          {learningFocus.length > 0 && (
            <section className="lifeos-rec-section" aria-labelledby="learning-heading">
              <div className="lifeos-rec-section__header">
                <div className="lifeos-rec-section__title-wrap">
                  <span className="lifeos-kicker">GROWTH & MASTERY</span>
                  <h2 id="learning-heading" className="lifeos-rec-section__title">
                    Stalled Learning Subjects
                  </h2>
                </div>
                <span className="lifeos-rec-section__badge">
                  {learningFocus.length} {learningFocus.length === 1 ? 'subject' : 'subjects'}
                </span>
              </div>

              <div className="lifeos-rec-card-list">
                {learningFocus.map((learn) => (
                  <div key={learn.id} className="lifeos-rec-card lifeos-rec-card--learning">
                    <div className="lifeos-rec-card__top">
                      <div className="lifeos-rec-card__info">
                        <span className="lifeos-rec-card__type">Learning Focus</span>
                        <h3 className="lifeos-rec-card__title">{learn.title}</h3>
                        <span className="lifeos-rec-card__subtitle">{learn.subtitle}</span>
                      </div>
                      <Button
                        variant="ghost"
                        size="small"
                        onClick={() => navigate(learn.actionUrl)}
                      >
                        Record Session →
                      </Button>
                    </div>

                    <div className="lifeos-rec-card__explanation">
                      <span className="lifeos-rec-why-tag">Why:</span> {learn.primaryReason}
                    </div>

                    {learn.reasons && learn.reasons.length > 1 && (
                      <ul className="lifeos-rec-card__reasons-list">
                        {learn.reasons.slice(1).map((r, i) => (
                          <li key={i}>{r}</li>
                        ))}
                      </ul>
                    )}
                  </div>
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
