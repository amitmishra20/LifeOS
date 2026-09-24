import React from 'react';
import heroDayImg from '../../assets/hero_mountain_day.png';
import heroNightImg from '../../assets/hero_mountain_night.png';
import './LifeHero.css';

/**
 * LifeHero Experience Component
 * The visual opening of LifeOS.
 * Atmospheric landscape integration, editorial scale, time-of-day awareness,
 * and state-driven headline/contextual statement. Zero KPI wall clutter.
 */
export const LifeHero = ({ identity, lifeState }) => {
  if (!identity || !lifeState) return null;

  const { name } = identity;
  const {
    headlineStatement,
    contextStatement,
    timeOfDayGreeting,
    momentumScore,
    dateString,
  } = lifeState;

  return (
    <section 
      className="lifeos-life-hero"
      style={{ '--hero-day-image': `url(${heroDayImg})`, '--hero-night-image': `url(${heroNightImg})` }}
      aria-label="Current Life State"
    >
      <div className="lifeos-life-hero__gradient-mask" />

      <div className="lifeos-life-hero__content">
        <div className="lifeos-life-hero__main">
          <div className="lifeos-life-hero__meta">
            <span className="lifeos-life-hero__sprout" aria-hidden="true">🌱</span>
            <span className="lifeos-life-hero__greeting">{timeOfDayGreeting}, {name}</span>
            <span className="lifeos-life-hero__separator">·</span>
            <span className="lifeos-life-hero__date">{dateString}</span>
          </div>

          <h1 className="lifeos-life-hero__headline">
            {headlineStatement}
          </h1>

          <p className="lifeos-life-hero__statement">
            {contextStatement}
          </p>
        </div>

        {/* Momentum Dial (Calculated Composite Progress) */}
        {momentumScore !== undefined && (
          <div className="lifeos-life-hero__aside">
            <div className="lifeos-momentum-surface" title={`Daily Momentum: ${momentumScore}% based on active tasks and habits`}>
              <div className="lifeos-momentum-ring-wrap">
                <svg className="lifeos-momentum-svg" width="60" height="60" viewBox="0 0 60 60">
                  <circle
                    className="lifeos-momentum-track"
                    cx="30"
                    cy="30"
                    r="24"
                  />
                  <circle
                    className="lifeos-momentum-fill"
                    cx="30"
                    cy="30"
                    r="24"
                    strokeDasharray="150.79"
                    strokeDashoffset={150.79 - (150.79 * Math.min(Math.max(momentumScore, 0), 100)) / 100}
                  />
                </svg>
                <span className="lifeos-momentum-value">{momentumScore}%</span>
              </div>
              <div className="lifeos-momentum-meta">
                <span className="lifeos-momentum-title">Momentum</span>
                <span className="lifeos-momentum-subtitle">Daily Cadence</span>
              </div>
            </div>
          </div>
        )}
      </div>
    </section>
  );
};

export default LifeHero;
