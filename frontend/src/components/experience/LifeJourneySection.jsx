import React from 'react';
import JourneyWaypoint from '../composites/JourneyWaypoint';
import './LifeJourneySection.css';

/**
 * LifeJourneySection Experience Component
 * The spatial LifeOS differentiator.
 * Renders the topological waypoint progression from Current Position to the Strategic Goal Destination.
 * Supports interactive unfolding of milestone tasks along the visual path.
 */
export const LifeJourneySection = ({
  journeyData,
  expandedWaypointId,
  onToggleWaypoint,
  onToggleTask,
}) => {
  if (!journeyData || !journeyData.hasJourney) {
    return (
      <section className="lifeos-journey-section" aria-label="Life Journey">
        <div className="lifeos-section-header">
          <div className="lifeos-section-header__left">
            <h2 className="lifeos-section-header__title">Your Journey</h2>
          </div>
        </div>
        <div className="lifeos-journey-empty">
          <p>No active journey trail. Establish a strategic goal to map milestones.</p>
        </div>
      </section>
    );
  }

  const { waypoints, destinationGoal } = journeyData;

  return (
    <section className="lifeos-journey-section" aria-label="Life Journey">
      <div className="lifeos-section-header">
        <div className="lifeos-section-header__left">
          <h2 className="lifeos-section-header__title">Your Journey</h2>
          <p className="lifeos-section-header__subtitle">
            {destinationGoal ? `Trajectory toward: ${destinationGoal.title}` : 'From where you are, to where you want to be.'}
          </p>
        </div>
        <div className="lifeos-section-header__right">
          <span className="lifeos-journey-hint">Click milestone to unfold actions</span>
        </div>
      </div>

      <div className="lifeos-journey-canvas">
        {/* Curved Topology Trail SVG */}
        <div className="lifeos-journey-svg-trail" aria-hidden="true">
          <svg className="lifeos-journey-svg" viewBox="0 0 800 60" preserveAspectRatio="none">
            <path
              d="M 60 30 Q 240 10 420 30 T 740 30"
              fill="none"
              stroke="rgba(212, 163, 115, 0.35)"
              strokeWidth="2"
              strokeDasharray="4 4"
            />
          </svg>
        </div>

        {/* Waypoints Sequence */}
        <div className="lifeos-journey-waypoints-container">
          {waypoints.map((wp) => (
            <JourneyWaypoint
              key={wp.id}
              waypoint={wp}
              isExpanded={expandedWaypointId === wp.id}
              onClick={() => onToggleWaypoint && onToggleWaypoint(wp.id)}
              onToggleTask={onToggleTask}
            />
          ))}
        </div>
      </div>
    </section>
  );
};

export default LifeJourneySection;
