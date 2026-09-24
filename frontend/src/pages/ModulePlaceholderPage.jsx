import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Surface from '../components/ui/Surface';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';

export const ModulePlaceholderPage = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const getModuleInfo = (path) => {
    switch (path) {
      case '/focus/today':
        return { title: "Today's Focus", phase: 'Phase 6', desc: 'Curated daily execution list powered by goal velocity and active habits.' };
      case '/focus/tasks':
        return { title: 'Action Items & Tasks', phase: 'Phase 6', desc: 'Atomic execution actions aligned to milestones with smart dependencies.' };
      case '/focus/habits':
        return { title: 'Habits & Routines', phase: 'Phase 7', desc: 'Daily consistency tracking, micro-streaks, and identity-reinforcing loops.' };
      case '/plan/goals':
        return { title: 'Strategic Goals', phase: 'Phase 5', desc: 'Long-term life outcomes, target timelines, and categorical milestones.' };
      case '/plan/calendar':
        return { title: 'Life Calendar', phase: 'Phase 9', desc: 'Time-block orchestration, milestone pacing, and commitments.' };
      case '/grow/learning':
        return { title: 'Continuous Learning', phase: 'Phase 8', desc: 'Skill trees, reading lists, knowledge synthesis, and course tracking.' };
      case '/grow/progress':
        return { title: 'Progress & Momentum', phase: 'Phase 10', desc: 'Velocity charts, milestone completions, and life quadrant distribution.' };
      case '/reflect/analytics':
        return { title: 'Life Analytics', phase: 'Phase 10', desc: 'Deep systemic signals on focus, productivity, and goal attainment rates.' };
      case '/reflect/health':
        return { title: 'Deterministic Goal Health', phase: 'Phase 11', desc: 'Rule 16 deterministic health evaluation: On Track, At Risk, Behind.' };
      case '/reflect/recommendations':
        return { title: 'Focus Recommendations', phase: 'Phase 12', desc: 'Context-aware suggestions based on velocity, streaks, and deadlines.' };
      case '/capture/notes':
        return { title: 'Notes & Idea Capture', phase: 'Phase 9', desc: 'Quick cognitive offloading, daily logs, and reflection entries.' };
      default:
        return { title: 'LifeOS Workspace', phase: 'Upcoming Phase', desc: 'Module reserved for planned architecture expansion.' };
    }
  };

  const info = getModuleInfo(location.pathname);

  return (
    <div style={{ maxWidth: '720px', margin: '60px auto 0', display: 'flex', flexDirection: 'column', gap: 'var(--space-6)' }}>
      <Surface variant="card" style={{ padding: 'var(--space-8)', textAlign: 'center', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 'var(--space-4)' }}>
        <Badge variant="warm" size="sm">
          <span>Scheduled for {info.phase}</span>
        </Badge>

        <h2 style={{ fontSize: 'var(--font-size-2xl)', fontWeight: 'var(--font-weight-semibold)', color: 'var(--color-text-primary)', margin: 0, letterSpacing: 'var(--tracking-tight)' }}>
          {info.title}
        </h2>

        <p style={{ fontSize: 'var(--font-size-sm)', color: 'var(--color-text-secondary)', maxWidth: '480px', lineHeight: 1.6, margin: 0 }}>
          {info.desc}
        </p>

        <div style={{ display: 'flex', gap: 'var(--space-3)', marginTop: 'var(--space-4)' }}>
          <Button variant="primary" size="md" onClick={() => navigate('/')}>
            Return to Dashboard
          </Button>
          <Button variant="outline" size="md" onClick={() => navigate('/dev/foundation')}>
            Foundation Diagnostics
          </Button>
        </div>
      </Surface>
    </div>
  );
};

export default ModulePlaceholderPage;
