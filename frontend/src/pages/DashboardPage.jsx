import React, { useState } from 'react';
import { useDashboardViewModel } from '../viewmodels/useDashboardViewModel';
import heroImage from '../assets/hero_sunset_valley.jpg';
import './DashboardPage.css';

const journeySteps = [
  { label: 'Software internship', detail: 'Destination', tone: 'destination' },
  { label: 'Portfolio projects', detail: 'Milestone 02', tone: 'progress' },
  { label: 'ShopSync UI', detail: 'Milestone 01', tone: 'active' },
  { label: 'Today\'s action', detail: 'Right now', tone: 'current' },
];

const habitLabels = ['DSA practice', 'Coding practice', 'Exercise', 'Reading'];

export const DashboardPage = () => {
  const { viewModel, toggleTask, toggleHabit, isLoading, error, retry } = useDashboardViewModel();
  const [notificationMessage, setNotificationMessage] = useState(null);
  const { focus, rhythm } = viewModel;
  const primaryTask = focus?.primaryFocus?.task;
  const supportingTasks = focus?.supportingTasks || [];

  const notify = (message) => {
    setNotificationMessage(message);
    window.setTimeout(() => setNotificationMessage(null), 2800);
  };

  if (isLoading) {
    return <main className="lifeos-dashboard-env lifeos-dashboard-loading" aria-busy="true"><div className="lifeos-loading-line" /><div className="lifeos-loading-field" /></main>;
  }

  if (error) {
    return <main className="lifeos-dashboard-env"><div className="lifeos-dashboard-error-surface" role="alert"><span className="lifeos-kicker">The day is out of reach</span><h1>We couldn&apos;t load your workspace.</h1><p>{error}</p><button className="lifeos-text-button" type="button" onClick={retry}>Try again</button></div></main>;
  }

  return (
    <main className="lifeos-dashboard-env" aria-label="Amit's LifeOS workspace">
      {notificationMessage && <div className="lifeos-feedback-toast" role="status">{notificationMessage}</div>}

      <section className="lifeos-opening" style={{ '--hero-image': `url(${heroImage})` }}>
        <div className="lifeos-opening__wash" />
        <div className="lifeos-opening__content">
          <span className="lifeos-kicker">Tuesday · 23 September 2026 · New York</span>
          <p className="lifeos-opening__greeting">Good morning, Amit.</p>
          <h1>Make something<br /><em>worth arriving at.</em></h1>
          <p className="lifeos-opening__subline">One deliberate step toward a software engineering internship.</p>
        </div>
        <div className="lifeos-opening__signal"><span>Today&apos;s direction</span><strong>Build, then ship.</strong></div>
      </section>

      <section className="lifeos-focus" aria-labelledby="focus-heading">
        <div className="lifeos-section-index"><span>01</span><span className="lifeos-rule" /><span>FOCUS</span></div>
        <div className="lifeos-focus__body">
          <div className="lifeos-focus__intro">
            <span className="lifeos-kicker">The next meaningful move</span>
            <h2 id="focus-heading">Build ShopSync<br /><em>UI components.</em></h2>
            <p>Reusable pieces for the portfolio project that moves you closer to the internship.</p>
            <button className="lifeos-primary-action" type="button" onClick={() => { if (primaryTask) toggleTask(primaryTask.id); notify(primaryTask?.completed ? 'Focus reopened.' : 'Focus marked complete.'); }}>
              <span>{primaryTask?.completed ? 'Reopen focus' : 'Start focus'}</span><span aria-hidden="true">↗</span>
            </button>
          </div>
          <div className="lifeos-focus__landscape" style={{ '--hero-image': `url(${heroImage})` }}>
            <span className="lifeos-focus__duration">45 min</span>
            <div className="lifeos-focus__caption"><span>Milestone 02</span><strong>Build portfolio projects</strong></div>
            <div className="lifeos-progress-orbit"><span>in motion</span><b /></div>
          </div>
        </div>
      </section>

      <section className="lifeos-journey" aria-labelledby="journey-heading">
        <div className="lifeos-section-index"><span>02</span><span className="lifeos-rule" /><span>DIRECTION</span></div>
        <div className="lifeos-journey__header"><div><span className="lifeos-kicker">Keep the horizon in view</span><h2 id="journey-heading">The road ahead</h2></div><span className="lifeos-journey__goal">Land a Software Engineering Internship <span>↗</span></span></div>
        <div className="lifeos-path" aria-label="Journey from today's action to software internship">
          <div className="lifeos-path__line" />
          {journeySteps.map((step, index) => <div className={`lifeos-path__step lifeos-path__step--${step.tone}`} key={step.label}><div className="lifeos-path__marker">{index === 3 ? '→' : index === 0 ? '✦' : String(index).padStart(2, '0')}</div><span>{step.detail}</span><strong>{step.label}</strong></div>)}
        </div>
      </section>

      <section className="lifeos-lower" aria-label="Progress and rhythm">
        <div className="lifeos-progress">
          <div className="lifeos-section-index"><span>03</span><span className="lifeos-rule" /><span>PROGRESS</span></div>
          <div className="lifeos-progress__heading"><h2>Small proof,<br /><em>every day.</em></h2><span>Portfolio project<br />01 / 04 milestones</span></div>
          <div className="lifeos-milestone-list">
            {['Strengthen React fundamentals', 'Build portfolio projects', 'Prepare for interviews', 'Apply consistently'].map((label, index) => <div className={`lifeos-milestone ${index === 1 ? 'is-active' : ''}`} key={label}><span className="lifeos-milestone__dot">{index < 1 ? '✓' : index + 1}</span><div><strong>{label}</strong><span>{index < 1 ? 'In progress' : index === 1 ? "Today's territory" : 'Up next'}</span></div><span className="lifeos-milestone__line" /></div>)}
          </div>
        </div>
        <div className="lifeos-rhythm">
          <div className="lifeos-section-index"><span>04</span><span className="lifeos-rule" /><span>RHYTHM</span></div>
          <h2>Keep showing up.</h2>
          <div className="lifeos-week"><span>THIS WEEK</span><div>{(rhythm?.weekDays || []).map((day, index) => <i className={day.isToday ? 'is-today' : ''} key={day.key}>{day.label}</i>)}</div></div>
          <div className="lifeos-habit-list">{habitLabels.map((label, index) => { const habit = rhythm?.habits?.[index]; return <button className="lifeos-habit" type="button" key={label} onClick={() => habit && toggleHabit(habit.id, 0)}><span className="lifeos-habit__mark">{habit?.history?.[0] ? '✓' : '○'}</span><span>{label}</span><b>{habit?.consistencyRate || 0}%</b></button>; })}</div>
        </div>
      </section>

      <section className="lifeos-next" aria-label="Supporting actions"><span className="lifeos-kicker">When focus softens</span><div className="lifeos-next__items">{supportingTasks.slice(0, 3).map((task) => <button type="button" className={`lifeos-next__item ${task.completed ? 'is-complete' : ''}`} key={task.id} onClick={() => toggleTask(task.id)}><span>{task.completed ? '✓' : '○'}</span><strong>{task.title}</strong><small>{task.time} · {task.category}</small></button>)}</div></section>
    </main>
  );
};

export default DashboardPage;
