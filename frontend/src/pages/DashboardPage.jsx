import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/useAuth';
import { useDashboardViewModel } from '../viewmodels/useDashboardViewModel';
import heroDayImage from '../assets/hero_mountain_day.png';
import heroNightImage from '../assets/hero_atmosphere.jpg';
import './DashboardPage.css';

const EmptyAction = ({ children, onClick }) => <button className="lifeos-primary-action" type="button" onClick={onClick}><span>{children}</span><span aria-hidden="true">↗</span></button>;

export const DashboardPage = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { viewModel, toggleTask, toggleHabit, isLoading, error, retry } = useDashboardViewModel(user);
  const [notificationMessage, setNotificationMessage] = useState(null);
  const { focus, rhythm, journey, goals, lifeState } = viewModel;
  const hasData = goals.hasGoals || focus.hasTasks || rhythm.hasHabits;
  const notify = (message) => { setNotificationMessage(message); window.setTimeout(() => setNotificationMessage(null), 2400); };
  const openCreate = (type) => {
    if (type === 'Goal') {
      navigate('/goals');
      return;
    }
    if (type === 'Task') {
      navigate('/tasks');
      return;
    }
    notify(`${type} creation is ready to connect.`);
  };

  if (isLoading) return <main className="lifeos-dashboard-env lifeos-dashboard-loading" aria-busy="true"><div className="lifeos-loading-line" /><div className="lifeos-loading-field" /></main>;
  if (error) return <main className="lifeos-dashboard-env"><div className="lifeos-dashboard-error-surface" role="alert"><span className="lifeos-kicker">Workspace unavailable</span><h1>We couldn&apos;t load your workspace.</h1><p>{error}</p><button className="lifeos-text-button" type="button" onClick={retry}>Try again</button></div></main>;

  return <main className="lifeos-dashboard-env" aria-label="LifeOS workspace">
    {notificationMessage && <div className="lifeos-feedback-toast" role="status">{notificationMessage}</div>}
    <section className="lifeos-opening" style={{ '--hero-day-image': `url(${heroDayImage})`, '--hero-night-image': `url(${heroNightImage})` }}>
      <div className="lifeos-opening__wash" /><div className="lifeos-opening__content">
        <span className="lifeos-kicker">{lifeState.dateString}</span><p className="lifeos-opening__greeting">Welcome to LifeOS{user?.name ? `, ${user.name}` : ''}.</p>
        <h1>{lifeState.headlineStatement === 'YOUR LIFE STARTS HERE' ? <>Your life,<br /><em>on your terms.</em></> : <>Make something<br /><em>worth arriving at.</em></>}</h1>
        <p className="lifeos-opening__subline">{lifeState.contextStatement}</p>
      </div><div className="lifeos-opening__signal"><span>{hasData ? 'Today&apos;s direction' : 'A calm place to begin'}</span><strong>{hasData ? 'Keep moving.' : 'Start with one thing.'}</strong></div>
    </section>

    <section className="lifeos-focus" aria-labelledby="focus-heading"><div className="lifeos-section-index"><span>01</span><span className="lifeos-rule" /><span>FOCUS</span></div>
      {!focus.primaryFocus ? <div className="lifeos-empty-panel"><div><span className="lifeos-kicker">Today&apos;s focus</span><h2 id="focus-heading">Nothing needs your attention<br /><em>yet.</em></h2><p>Choose one meaningful thing to move forward. Your focus will appear here.</p></div><div className="lifeos-empty-actions"><EmptyAction onClick={() => openCreate('Task')}>Create a task</EmptyAction><button className="lifeos-text-button" type="button" onClick={() => openCreate('Goal')}>Set a goal</button></div></div> : <div className="lifeos-focus__body"><div className="lifeos-focus__intro"><span className="lifeos-kicker">The next meaningful move</span><h2 id="focus-heading">{focus.primaryFocus.task.title}</h2><p>{focus.primaryFocus.whyReason}</p><EmptyAction onClick={() => { toggleTask(focus.primaryFocus.task.id); notify('Focus updated.'); }}>{focus.primaryFocus.task.completed ? 'Reopen focus' : 'Complete focus'}</EmptyAction></div><div className="lifeos-focus__landscape" style={{ '--hero-day-image': `url(${heroDayImage})`, '--hero-night-image': `url(${heroNightImage})` }}><span className="lifeos-focus__duration">{focus.primaryFocus.task.duration || 'Today'}</span><div className="lifeos-focus__caption"><span>{focus.primaryFocus.goal?.title || 'Personal focus'}</span><strong>{focus.primaryFocus.milestone?.title || 'Independent action'}</strong></div></div></div>}
    </section>

    <section className="lifeos-journey" aria-labelledby="journey-heading"><div className="lifeos-section-index"><span>02</span><span className="lifeos-rule" /><span>DIRECTION</span></div>
      {!journey.hasJourney ? <div className="lifeos-empty-panel lifeos-empty-panel--compact"><div><span className="lifeos-kicker">Your life map</span><h2 id="journey-heading">Your direction will take shape<br /><em>as you choose it.</em></h2><p>Create a goal to give this space a horizon.</p></div><EmptyAction onClick={() => openCreate('Goal')}>Create your first goal</EmptyAction></div> : <div className="lifeos-journey__header"><div><span className="lifeos-kicker">Keep the horizon in view</span><h2 id="journey-heading">The road ahead</h2></div><span className="lifeos-journey__goal">{journey.destinationGoal.title} <span>↗</span></span></div>}
    </section>

    <section className="lifeos-lower" aria-label="Progress and rhythm"><div className="lifeos-progress"><div className="lifeos-section-index"><span>03</span><span className="lifeos-rule" /><span>PROGRESS</span></div><div className="lifeos-empty-panel lifeos-empty-panel--bare"><div><h2>Your progress will take shape<br /><em>as you begin moving.</em></h2><p>No milestones or completion data yet.</p></div><EmptyAction onClick={() => openCreate('Goal')}>Create your first goal</EmptyAction></div></div>
      <div className="lifeos-rhythm"><div className="lifeos-section-index"><span>04</span><span className="lifeos-rule" /><span>RHYTHM</span></div><h2>{rhythm.hasHabits ? 'Keep showing up.' : <>Build a rhythm<br /><em>that feels like yours.</em></>}</h2>{!rhythm.hasHabits ? <div className="lifeos-empty-rhythm"><p>Small practices become support for the life you&apos;re creating.</p><EmptyAction onClick={() => openCreate('Habit')}>Create a habit</EmptyAction></div> : <div className="lifeos-habit-list">{rhythm.habits.map((habit) => <button className="lifeos-habit" type="button" key={habit.id} onClick={() => toggleHabit(habit.id, 0)}><span className="lifeos-habit__mark">{habit.history[0] ? '✓' : '○'}</span><span>{habit.title}</span><b>{habit.consistencyRate}%</b></button>)}</div>}</div>
    </section>
    {focus.supportingTasks.length > 0 && <section className="lifeos-next" aria-label="Supporting actions"><span className="lifeos-kicker">More from your day</span><div className="lifeos-next__items">{focus.supportingTasks.slice(0, 3).map((task) => <button type="button" className={`lifeos-next__item ${task.completed ? 'is-complete' : ''}`} key={task.id} onClick={() => toggleTask(task.id)}><span>{task.completed ? '✓' : '○'}</span><strong>{task.title}</strong><small>{task.time || 'Today'} · {task.category || 'Task'}</small></button>)}</div></section>}
  </main>;
};

export default DashboardPage;
