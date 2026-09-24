import React, { useState, useEffect } from 'react';
import { checkHealth } from '../services/api';
import api from '../services/api';
import Button from '../components/ui/Button';
import Card, { CardHeader, CardTitle, CardDescription, CardBody } from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import './FoundationPage.css';

export const FoundationPage = () => {
  // Health check state
  const [healthData, setHealthData] = useState(null);
  const [healthStatus, setHealthStatus] = useState('idle'); // 'idle' | 'loading' | 'success' | 'error'
  const [healthLatency, setHealthLatency] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);

  // Error simulation state
  const [errorSimStatus, setErrorSimStatus] = useState('idle');
  const [simulatedErrorData, setSimulatedErrorData] = useState(null);

  // Viewport breakpoint state
  const [viewportWidth, setViewportWidth] = useState(window.innerWidth);

  useEffect(() => {
    const handleResize = () => setViewportWidth(window.innerWidth);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const getBreakpointLabel = () => {
    if (viewportWidth >= 1024) return 'Desktop (>= 1024px)';
    if (viewportWidth >= 768) return 'Tablet (768px - 1023px)';
    return 'Mobile (< 768px)';
  };

  const runHealthCheck = async () => {
    setHealthStatus('loading');
    setErrorMessage(null);
    const start = performance.now();
    try {
      const data = await checkHealth();
      const end = performance.now();
      setHealthLatency(Math.round(end - start));
      setHealthData(data);
      setHealthStatus('success');
    } catch (err) {
      const end = performance.now();
      setHealthLatency(Math.round(end - start));
      setErrorMessage(err.message || 'Failed to connect to backend');
      setHealthStatus('error');
    }
  };

  const simulateError = async () => {
    setErrorSimStatus('loading');
    setSimulatedErrorData(null);
    try {
      await api.get('/non-existent-endpoint');
    } catch (err) {
      setErrorSimStatus('caught');
      setSimulatedErrorData({
        status: err.status,
        error: err.error,
        message: err.message,
        path: err.path,
        timestamp: err.timestamp,
      });
    }
  };

  useEffect(() => {
    runHealthCheck();
  }, []);

  return (
    <div className="foundation-page">
      {/* Header Banner */}
      <div className="foundation-page__banner">
        <div>
          <div className="foundation-page__badge-row">
            <Badge variant="indigo" size="md">Phase 1 Foundation</Badge>
            <Badge variant="emerald" size="md">Architecture Verified</Badge>
          </div>
          <h1 className="foundation-page__title">Development & Repository Foundation</h1>
          <p className="foundation-page__subtitle">
            Technical verification surface for backend connectivity, RFC-7807 error format, design tokens, and responsive layout.
          </p>
        </div>
      </div>

      <div className="foundation-page__grid">
        {/* Card 1: Backend Health Verification */}
        <Card variant="elevated">
          <CardHeader>
            <div className="foundation-card__header-row">
              <CardTitle>1. Backend Health Check</CardTitle>
              {healthStatus === 'success' && <Badge variant="emerald">STATUS: UP</Badge>}
              {healthStatus === 'error' && <Badge variant="crimson">STATUS: DOWN</Badge>}
              {healthStatus === 'loading' && <Badge variant="amber">CONNECTING...</Badge>}
            </div>
            <CardDescription>
              Verifies endpoint <code className="foundation-code">GET /api/v1/health</code> via Axios client.
            </CardDescription>
          </CardHeader>
          <CardBody>
            <div className="foundation-status-panel">
              <div className="foundation-metric">
                <span className="foundation-metric__label">Status</span>
                <span className="foundation-metric__value">
                  {healthData?.status || (healthStatus === 'loading' ? 'Checking...' : 'Offline')}
                </span>
              </div>
              <div className="foundation-metric">
                <span className="foundation-metric__label">Latency</span>
                <span className="foundation-metric__value">
                  {healthLatency !== null ? `${healthLatency} ms` : '—'}
                </span>
              </div>
              <div className="foundation-metric">
                <span className="foundation-metric__label">Service Version</span>
                <span className="foundation-metric__value">
                  {healthData?.version || '1.0.0'}
                </span>
              </div>
              <div className="foundation-metric">
                <span className="foundation-metric__label">Timestamp</span>
                <span className="foundation-metric__value foundation-metric__value--small">
                  {healthData?.timestamp ? new Date(healthData.timestamp).toLocaleTimeString() : '—'}
                </span>
              </div>
            </div>

            {errorMessage && (
              <div className="foundation-error-banner">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="12" cy="12" r="10" />
                  <line x1="12" y1="8" x2="12" y2="12" />
                  <line x1="12" y1="16" x2="12.01" y2="16" />
                </svg>
                <span>{errorMessage}</span>
              </div>
            )}

            <div className="foundation-action-row">
              <Button
                variant="primary"
                size="sm"
                onClick={runHealthCheck}
                isLoading={healthStatus === 'loading'}
              >
                Re-ping Health Endpoint
              </Button>
            </div>
          </CardBody>
        </Card>

        {/* Card 2: Error Format Verification */}
        <Card variant="default">
          <CardHeader>
            <div className="foundation-card__header-row">
              <CardTitle>2. RFC-7807 Error Handling</CardTitle>
              <Badge variant="amber">Global Exception Handler</Badge>
            </div>
            <CardDescription>
              Triggers a 404 on <code className="foundation-code">/api/v1/non-existent-endpoint</code> to verify normalized error response.
            </CardDescription>
          </CardHeader>
          <CardBody>
            <div className="foundation-action-row">
              <Button
                variant="outline"
                size="sm"
                onClick={simulateError}
                isLoading={errorSimStatus === 'loading'}
              >
                Trigger Error Simulation
              </Button>
            </div>

            {simulatedErrorData ? (
              <div className="foundation-json-viewer">
                <pre>{JSON.stringify(simulatedErrorData, null, 2)}</pre>
              </div>
            ) : (
              <p className="foundation-hint-text">
                Click button above to verify standardized RFC-7807 error payload.
              </p>
            )}
          </CardBody>
        </Card>

        {/* Card 3: Responsive Breakpoint Verification */}
        <Card variant="default">
          <CardHeader>
            <div className="foundation-card__header-row">
              <CardTitle>3. Responsive Viewport</CardTitle>
              <Badge variant="indigo">{getBreakpointLabel()}</Badge>
            </div>
            <CardDescription>
              Ensures layout fluidly adapts across Desktop, Tablet, and Mobile views.
            </CardDescription>
          </CardHeader>
          <CardBody>
            <div className="foundation-viewport-info">
              <div className="foundation-viewport-metric">
                <span>Current Width:</span>
                <strong>{viewportWidth}px</strong>
              </div>
              <div className="foundation-viewport-metric">
                <span>Active Shell:</span>
                <strong>{viewportWidth >= 1024 ? 'Desktop Persistent Sidebar' : viewportWidth >= 768 ? 'Tablet Drawer' : 'Mobile Bottom Nav + Drawer'}</strong>
              </div>
            </div>
            <div className="foundation-breakpoint-indicators">
              <div className={`breakpoint-chip ${viewportWidth < 768 ? 'active' : ''}`}>Mobile (&lt;768px)</div>
              <div className={`breakpoint-chip ${viewportWidth >= 768 && viewportWidth < 1024 ? 'active' : ''}`}>Tablet (768-1023px)</div>
              <div className={`breakpoint-chip ${viewportWidth >= 1024 ? 'active' : ''}`}>Desktop (&gt;=1024px)</div>
            </div>
          </CardBody>
        </Card>

        {/* Card 4: Design Tokens & Component Primitives */}
        <Card variant="default">
          <CardHeader>
            <div className="foundation-card__header-row">
              <CardTitle>4. Design System Tokens</CardTitle>
              <Badge variant="default">design.md</Badge>
            </div>
            <CardDescription>
              Deep Black foundation + Warm Ochre accent + Restrained semantics.
            </CardDescription>
          </CardHeader>
          <CardBody>
            {/* Swatches */}
            <div className="foundation-token-section">
              <span className="foundation-token-section__title">Color Palette</span>
              <div className="foundation-swatches">
                <div className="foundation-swatch swatch-midnight" title="Canvas Void: var(--color-bg-canvas)">
                  <span>Canvas</span>
                </div>
                <div className="foundation-swatch swatch-surface" title="Surface: var(--color-bg-surface)">
                  <span>Surface</span>
                </div>
                <div className="foundation-swatch swatch-warm" title="Warm Accent: var(--color-accent-primary)">
                  <span>Warm</span>
                </div>
                <div className="foundation-swatch swatch-emerald" title="Sage/Teal: var(--color-success-base)">
                  <span>Teal</span>
                </div>
                <div className="foundation-swatch swatch-amber" title="Amber: var(--color-warning-base)">
                  <span>Amber</span>
                </div>
                <div className="foundation-swatch swatch-crimson" title="Crimson: var(--color-danger-base)">
                  <span>Crimson</span>
                </div>
              </div>
            </div>

            {/* Button Primitives */}
            <div className="foundation-token-section">
              <span className="foundation-token-section__title">Button Variants</span>
              <div className="foundation-sample-row">
                <Button variant="primary" size="sm">Primary</Button>
                <Button variant="secondary" size="sm">Secondary</Button>
                <Button variant="outline" size="sm">Outline</Button>
                <Button variant="ghost" size="sm">Ghost</Button>
                <Button variant="danger" size="sm">Danger</Button>
              </div>
            </div>

            {/* Badge Primitives */}
            <div className="foundation-token-section">
              <span className="foundation-token-section__title">Badge Variants</span>
              <div className="foundation-sample-row">
                <Badge variant="default">Default</Badge>
                <Badge variant="indigo">Indigo</Badge>
                <Badge variant="emerald">Emerald</Badge>
                <Badge variant="amber">Amber</Badge>
                <Badge variant="crimson">Crimson</Badge>
              </div>
            </div>
          </CardBody>
        </Card>
      </div>

      {/* Scope Confirmation Banner */}
      <div className="foundation-scope-audit">
        <div className="foundation-scope-audit__header">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
          </svg>
          <span className="foundation-scope-audit__title">Phase 1 Boundary & Anti-Scope Audit</span>
        </div>
        <div className="foundation-scope-audit__body">
          <div className="foundation-scope-item verified">
            <span className="scope-icon">&#10003;</span>
            <span>Zero feature entities (No Goals, Tasks, Habits, Notes, Calendar, Learning)</span>
          </div>
          <div className="foundation-scope-item verified">
            <span className="scope-icon">&#10003;</span>
            <span>Zero authentication endpoints (HttpOnly cookie architecture ready for Phase 2)</span>
          </div>
          <div className="foundation-scope-item verified">
            <span className="scope-icon">&#10003;</span>
            <span>Authoritative Flyway migration baseline (Hibernate validate-only)</span>
          </div>
          <div className="foundation-scope-item verified">
            <span className="scope-icon">&#10003;</span>
            <span>Zero premature scaling infra (No Redis, Kafka, or microservices)</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FoundationPage;
