import React, { Suspense, lazy } from 'react';
import './LifeWorld.css';

const TempleNightScene = lazy(() => import('./TempleNightScene'));

export default function LifeWorld() {
  return (
    <div className="lifeos-world-canvas" aria-hidden="true">
      <Suspense fallback={null}>
        <TempleNightScene />
      </Suspense>
    </div>
  );
}
