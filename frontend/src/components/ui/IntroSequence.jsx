import React, { useState, useEffect } from 'react';
import './IntroSequence.css';

export const IntroSequence = ({ onComplete }) => {
  const [phase, setPhase] = useState('assemble'); // 'assemble' -> 'emerge' -> 'dissolve' -> 'complete'

  useEffect(() => {
    // If reduced motion is preferred, immediately finish
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
      if (onComplete) onComplete();
      return;
    }

    const t1 = setTimeout(() => setPhase('emerge'), 800);
    const t2 = setTimeout(() => setPhase('dissolve'), 2200);
    const t3 = setTimeout(() => {
      setPhase('complete');
      if (onComplete) onComplete();
    }, 2800);

    return () => {
      clearTimeout(t1);
      clearTimeout(t2);
      clearTimeout(t3);
    };
  }, [onComplete]);

  if (phase === 'complete') return null;

  return (
    <div className={`lifeos-intro-curtain lifeos-intro-curtain--${phase}`} aria-hidden="true">
      {/* Environmental Horizon Wash */}
      <div className="lifeos-intro-env-layer" />
      <div className="lifeos-intro-atmosphere-mask" />

      {/* Identity & Typography Emergence */}
      <div className="lifeos-intro-stage">
        <div className="lifeos-intro-glyph">
          <svg
            width="38"
            height="38"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.75"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M11 20A7 7 0 0 1 4 13C4 7 9 3 17 3c1 5-1 11-6 17z" />
            <path d="M7 17l8-8" />
          </svg>
        </div>

        <div className="lifeos-intro-wordmark">
          <span className="lifeos-intro-label">PERSONAL OPERATING SYSTEM</span>
          <h1 className="lifeos-intro-title">LifeOS</h1>
          <p className="lifeos-intro-tagline">Where am I? What matters now? Where am I going?</p>
        </div>
      </div>
    </div>
  );
};

export default IntroSequence;
