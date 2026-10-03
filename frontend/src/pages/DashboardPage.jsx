import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/useAuth';
import { useDashboardViewModel } from '../viewmodels/useDashboardViewModel';
import Button from '../components/ui/Button';
import './DashboardPage.css';

const statusTone = (value = '') => value.toLowerCase().replace(/_/g, '-');

export const DashboardPage = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { viewModel, toggleTask, toggleHabit, isLoading, error, retry } = useDashboardViewModel(user);
  const [notificationMessage, setNotificationMessage] = useState(null);

  const { focus, rhythm, journey, goals, lifeState } = viewModel;
  const primaryTask = focus.primaryFocus?.task;
  const primaryGoal = focus.primaryFocus?.goal;
  const primaryMilestone = focus.primaryFocus?.milestone;
  const hasData = goals.hasGoals || focus.hasTasks || rhythm.hasHabits;

  const notify = (message) => {
    setNotificationMessage(message);
    window.clearTimeout(window.__lifeosToast);
    window.__lifeosToast = window.setTimeout(() => setNotificationMessage(null), 2200);
  };

  const openCreate = (type) => {
    const routes = { Goal: '/goals', Task: '/tasks', Habit: '/habits' };
    if (routes[type]) navigate(routes[type]);
  };

  if (isLoading) {
    return <main className="lifeos-world lifeos-world--loading" aria-busy="true"><div className="lifeos-world__loading-orbit" /><span>Entering your day…</span></main>;
  }

  if (error) {
    return (
      <main className="lifeos-world">
        <section className="lifeos-world-error" role="alert">
          <span className="lifeos-eyebrow">LifeOS</span>
          <h1>Your world could not be loaded.</h1>
          <p>{error}</p>
          <Button variant="primary" onClick={retry}>Try again</Button>
        </section>
      </main>
    );
  }

  const visibleWaypoints = journey.waypoints?.slice(0, 6) || [];
  const completedWaypoints = visibleWaypoints.filter((point) => point.status === 'completed').length;
  const totalWaypoints = Math.max(visibleWaypoints.length - 1, 0);
  const journeyProgress = totalWaypoints > 0 ? Math.round((completedWaypoints / totalWaypoints) * 100) : 0;
  const visibleRhythms = rhythm.habits?.slice(0, 3) || [];
  const visibleActions = focus.supportingTasks?.slice(0, 4) || [];

  return (
    <main className="lifeos-world" aria-label="LifeOS dashboard">
      {notificationMessage && <div className="lifeos-world__toast" role="status">{notificationMessage}</div>}

      <header className="lifeos-world__intro">
        <div>
          <span className="lifeos-eyebrow">{lifeState.dateString}</span>
          <p className="lifeos-world__greeting">{lifeState.timeOfDayGreeting}, {user?.name?.split(' ')[0] || 'there'}.</p>
          <h1>{hasData ? 'Build with intention.' : 'Begin with one thing.'}</h1>
          <p className="lifeos-world__lede">
            {hasData ? 'Your day is a small part of the life you are building.' : 'LifeOS becomes clearer as you give it a direction.'}
          </p>
        </div>
        <div className="lifeos-world__orientation" aria-label="Current orientation">
          <span>NOW</span>
          <strong>{hasData ? 'Keep moving' : 'Choose a direction'}</strong>
          <small>{lifeState.momentumScore}% daily momentum</small>
        </div>
      </header>

      <section className="lifeos-focus-scene" aria-labelledby="dashboard-focus-title">
        <div className="lifeos-focus-scene__content">
          <span className="lifeos-eyebrow">01 · Current focus</span>
          {primaryTask ? (
            <>
              <div className="lifeos-focus-scene__lineage">
                {primaryGoal && <button type="button" onClick={() => navigate(`/goals/${primaryGoal.id}`)}>{primaryGoal.title}</button>}
                {primaryMilestone && <><span>→</span><span>{primaryMilestone.title}</span></>}
              </div>
              <h2 id="dashboard-focus-title">{primaryTask.title}</h2>
              <p className="lifeos-focus-scene__reason">{focus.primaryFocus.whyReason}</p>
              <div className="lifeos-focus-scene__actions">
                <Button
                  variant="primary"
                  size="lg"
                  onClick={() => {
                    toggleTask(primaryTask.id);
                    notify(primaryTask.completed ? 'Focus reopened.' : 'Focus completed.');
                  }}
                >
                  {primaryTask.completed ? 'Reopen focus' : 'Complete focus'}
                </Button>
                {primaryTask.duration && <span>{primaryTask.duration}</span>}
                {primaryTask.dueDate && <span>Due {primaryTask.dueDate}</span>}
              </div>
            </>
          ) : (
            <>
              <h2 id="dashboard-focus-title">Nothing needs you yet.</h2>
              <p className="lifeos-focus-scene__reason">Choose one meaningful action and let it become today's anchor.</p>
              <div className="lifeos-focus-scene__actions">
                <Button variant="primary" onClick={() => openCreate('Task')}>Create a task</Button>
                <button type="button" className="lifeos-text-action" onClick={() => openCreate('Goal')}>Define a goal →</button>
              </div>
            </>
          )}
        </div>
        <div className="lifeos-focus-scene__edge" aria-hidden="true"><span>FOCUS</span></div>
      </section>

      <section className="lifeos-journey" aria-labelledby="dashboard-journey-title">
        <div className="lifeos-section-intro">
          <span className="lifeos-eyebrow">02 · Direction</span>
          <div>
            <h2 id="dashboard-journey-title">{journey.destinationGoal?.title || 'Your destination'}</h2>
            <p>{journey.hasJourney ? `${completedWaypoints} of ${totalWaypoints} waypoints reached · ${journeyProgress}%` : 'Give your next chapter a destination.'}</p>
          </div>
          {journey.destinationGoal && (
            <button type="button" className="lifeos-text-action" onClick={() => navigate(`/goals/${journey.destinationGoal.id}`)}>Enter world →</button>
          )}
        </div>

        {journey.hasJourney ? (
          <div className="lifeos-journey__path" style={{ '--waypoint-count': Math.max(visibleWaypoints.length, 1) }}>
            <div className="lifeos-journey__rail" aria-hidden="true" />
            {visibleWaypoints.map((point, index) => (
              <button
                type="button"
                key={point.id}
                className={`lifeos-waypoint lifeos-waypoint--${statusTone(point.status)} ${point.isCurrent ? 'is-current' : ''}`}
                onClick={() => point.type === 'destination' && journey.destinationGoal && navigate(`/goals/${journey.destinationGoal.id}`)}
                disabled={point.type !== 'destination'}
              >
                <span className="lifeos-waypoint__marker">{point.type === 'destination' ? '↗' : index === 0 ? '•' : index}</span>
                <span className="lifeos-waypoint__copy"><strong>{point.label}</strong><small>{point.sublabel}</small></span>
              </button>
            ))}
          </div>
        ) : (
          <button type="button" className="lifeos-open-space" onClick={() => openCreate('Goal')}>
            <span>+</span> Define a destination
          </button>
        )}
      </section>

      <section className="lifeos-rhythm-band" aria-labelledby="dashboard-rhythm-title">
        <div className="lifeos-rhythm-band__heading">
          <span className="lifeos-eyebrow">03 · Rhythm</span>
          <h2 id="dashboard-rhythm-title">The quiet work of becoming.</h2>
          <p>Your repeatable actions keep the larger journey moving.</p>
        </div>
        <div className="lifeos-rhythm-band__items">
          {visibleRhythms.length ? visibleRhythms.map((habit) => (
            <button
              type="button"
              className="lifeos-rhythm-line"
              key={habit.id}
              onClick={() => {
                toggleHabit(habit.id, new Date().toISOString().slice(0, 10));
                notify('Rhythm updated.');
              }}
            >
              <span className="lifeos-rhythm-line__name">{habit.title}</span>
              <span className="lifeos-rhythm-line__dots" aria-hidden="true">
                {(habit.history || []).slice(-7).map((day, idx) => <i key={`${habit.id}-${idx}`} className={day.completed ? 'is-done' : ''} />)}
              </span>
              <span className="lifeos-rhythm-line__meta">{habit.summaryText}</span>
            </button>
          )) : (
            <button type="button" className="lifeos-rhythm-empty" onClick={() => openCreate('Habit')}>Establish a rhythm →</button>
          )}
        </div>
      </section>

      {visibleActions.length > 0 && (
        <section className="lifeos-actions-band" aria-labelledby="dashboard-actions-title">
          <div>
            <span className="lifeos-eyebrow">04 · Nearby</span>
            <h2 id="dashboard-actions-title">Things that can move when you are ready.</h2>
          </div>
          <div className="lifeos-action-stream">
            {visibleActions.map((task) => (
              <button
                type="button"
                key={task.id}
                className={`lifeos-action-row ${task.completed ? 'is-complete' : ''}`}
                onClick={() => {
                  toggleTask(task.id);
                  notify(task.completed ? 'Action reopened.' : 'Action completed.');
                }}
              >
                <span className="lifeos-action-row__mark">{task.completed ? '✓' : '○'}</span>
                <span className="lifeos-action-row__title">{task.title}</span>
                <span className="lifeos-action-row__meta">{task.duration || task.time || 'Today'}</span>
              </button>
            ))}
          </div>
        </section>
      )}
    </main>
  );
};

export default DashboardPage;
