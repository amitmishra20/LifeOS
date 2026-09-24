import React, { useState } from 'react';
import { useDashboardViewModel } from '../viewmodels/useDashboardViewModel';
import LifeHero from '../components/experience/LifeHero';
import TodayFocusSection from '../components/experience/TodayFocusSection';
import LifeJourneySection from '../components/experience/LifeJourneySection';
import RhythmSection from '../components/experience/RhythmSection';
import GoalsOverviewSection from '../components/experience/GoalsOverviewSection';
import GuidanceCard from '../components/composites/GuidanceCard';
import './DashboardPage.css';

/**
 * DashboardPage (Thin Orchestrator)
 * LifeOS Core Workspace Surface
 * 
 * Orchestrates:
 * 1. Data/Derived State via useDashboardViewModel
 * 2. Visual Information Architecture (Hero -> Today's Focus -> Journey -> Goals -> Rhythm -> Guidance)
 * 3. Lifecycle States (Active, Completed, Loading, Error)
 */
export const DashboardPage = () => {
  const {
    viewModel,
    toggleTask,
    toggleHabit,
    expandedWaypointId,
    toggleWaypoint,
    expandedGoalId,
    toggleGoal,
    isLoading,
    error,
    retry,
  } = useDashboardViewModel();

  const [notificationMessage, setNotificationMessage] = useState(null);

  // Handle action triggers from Guidance or Focus
  const handleTriggerAction = (actionType) => {
    let msg = 'Action triggered';
    if (actionType === 'PROTECT_BLOCK' || actionType === 'DEFAULT') {
      msg = 'Morning focus block protected on your calendar.';
    } else if (actionType === 'REST_AND_REFLECT') {
      msg = 'Entering quiet reflection mode. Focus alarms silenced.';
    } else if (actionType === 'CREATE_GOAL') {
      msg = 'Opening Goal Creation dialog...';
    }
    setNotificationMessage(msg);
    setTimeout(() => setNotificationMessage(null), 3200);
  };

  const handleStartAction = (taskId) => {
    setNotificationMessage(`Starting focused learning session for deliverable #${taskId}...`);
    setTimeout(() => setNotificationMessage(null), 3000);
  };

  // State: Loading
  if (isLoading) {
    return (
      <main className="lifeos-dashboard-env" aria-busy="true" aria-label="Loading Dashboard">
        <div className="lifeos-dashboard-loading-skeleton">
          <div className="lifeos-skeleton-hero" />
          <div className="lifeos-skeleton-grid">
            <div className="lifeos-skeleton-main" />
            <div className="lifeos-skeleton-side" />
          </div>
        </div>
      </main>
    );
  }

  // State: Error
  if (error) {
    return (
      <main className="lifeos-dashboard-env" role="alert" aria-label="Dashboard Error">
        <div className="lifeos-dashboard-error-surface">
          <div className="lifeos-error-icon">⚠️</div>
          <h2 className="lifeos-error-title">We couldn't load your day.</h2>
          <p className="lifeos-error-desc">{error}</p>
          <button type="button" className="lifeos-error-retry-btn" onClick={retry}>
            Try again
          </button>
        </div>
      </main>
    );
  }

  const { identity, lifeState, focus, journey, rhythm, guidance, goals } = viewModel;

  return (
    <main className="lifeos-dashboard-env" aria-label="Personal Operating System Dashboard">
      {/* Floating Action Feedback Notification */}
      {notificationMessage && (
        <div className="lifeos-feedback-toast" role="status" aria-live="polite">
          <span className="lifeos-feedback-toast__icon">✓</span>
          <span>{notificationMessage}</span>
        </div>
      )}

      {/* =====================================================================
          1. LIFE STATE / HERO (Where am I?)
          ===================================================================== */}
      <LifeHero identity={identity} lifeState={lifeState} />

      {/* =====================================================================
          2. TWO-COLUMN EDITORIAL WORKSPACE
          ===================================================================== */}
      <div className="lifeos-dashboard-grid">
        {/* MAIN COLUMN (Focus & Direction) */}
        <div className="lifeos-dashboard-main-col">
          {/* TODAY'S FOCUS (What matters? What should I do now?) */}
          <TodayFocusSection
            focusData={focus}
            lifeState={lifeState}
            onToggleTask={toggleTask}
            onStartAction={handleStartAction}
            onCreateGoal={() => handleTriggerAction('CREATE_GOAL')}
          />

          {/* LIFE JOURNEY (Am I moving?) */}
          <LifeJourneySection
            journeyData={journey}
            expandedWaypointId={expandedWaypointId}
            onToggleWaypoint={toggleWaypoint}
            onToggleTask={toggleTask}
          />

          {/* STRATEGIC GOALS OVERVIEW */}
          <GoalsOverviewSection
            goalsData={goals}
            expandedGoalId={expandedGoalId}
            onSelectGoal={(g) => toggleGoal(g.id)}
            onCreateGoal={() => handleTriggerAction('CREATE_GOAL')}
          />
        </div>

        {/* SIDE COLUMN (Supporting Context & Guidance) */}
        <aside className="lifeos-dashboard-side-col" aria-label="Context & Guidance">
          {/* CONTEXTUAL GUIDANCE (What should I understand?) */}
          <GuidanceCard
            guidance={guidance}
            onTriggerAction={handleTriggerAction}
          />

          {/* HABIT RHYTHM (Am I consistent?) */}
          <RhythmSection
            rhythmData={rhythm}
            onToggleHabit={toggleHabit}
          />
        </aside>
      </div>
    </main>
  );
};

export default DashboardPage;
