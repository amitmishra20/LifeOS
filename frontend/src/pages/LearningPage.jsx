import React, { useState, useEffect, useCallback, useMemo } from 'react';
import learningService from '../services/learningService';
import LearningModal from '../components/learning/LearningModal';
import LearningSessionModal from '../components/learning/LearningSessionModal';
import LearningSessionsDrawer from '../components/learning/LearningSessionsDrawer';
import Button from '../components/ui/Button';
import EmptyState from '../components/ui/EmptyState';
import LoadingState, { CardSkeleton } from '../components/ui/LoadingState';
import './LearningPage.css';

const FILTER_TABS = [
  { key: 'ACTIVE', label: 'Active Subjects' },
  { key: 'COMPLETED', label: 'Completed' },
  { key: 'PAUSED', label: 'Paused' },
  { key: 'ARCHIVED', label: 'Archived' },
];

const CATEGORY_ICONS = {
  TECHNICAL: '💻',
  LANGUAGE: '🌐',
  ACADEMIC: '📚',
  PROFESSIONAL: '💼',
  CREATIVE: '🎨',
  PERSONAL: '🌱',
};

export const LearningPage = () => {
  const [items, setItems] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('ACTIVE');

  // Item Modal (Create / Edit)
  const [isItemModalOpen, setIsItemModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Session Logging Modal
  const [isSessionModalOpen, setIsSessionModalOpen] = useState(false);
  const [sessionTargetItem, setSessionTargetItem] = useState(null);

  // Session Drawer
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const [drawerItem, setDrawerItem] = useState(null);
  const [drawerSessions, setDrawerSessions] = useState([]);
  const [isDrawerLoading, setIsDrawerLoading] = useState(false);

  // Toast
  const [toastMessage, setToastMessage] = useState(null);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3000);
  };

  const fetchItems = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await learningService.getLearningItems();
      setItems(data || []);
    } catch (err) {
      setError(err.message || 'Failed to load learning subjects.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchItems();
  }, [fetchItems]);

  // Derived Summary KPIs
  const summary = useMemo(() => {
    const activeItems = items.filter((i) => i.status === 'ACTIVE');
    const activeCount = activeItems.length;
    const totalMinutes = items.reduce((sum, i) => sum + (i.totalMinutesLearned || 0), 0);
    const totalHours = Math.round((totalMinutes / 60.0) * 10) / 10;
    const avgProgress = activeCount > 0
      ? Math.round(activeItems.reduce((sum, i) => sum + (i.currentProgress || 0), 0) / activeCount)
      : 0;

    return {
      activeCount,
      totalHours,
      avgProgress,
    };
  }, [items]);

  // Filtered Items
  const filteredItems = useMemo(() => {
    return items.filter((i) => i.status === activeTab);
  }, [items, activeTab]);

  // Refresh single item metrics
  const refreshItem = async (itemId) => {
    try {
      const updated = await learningService.getLearningItem(itemId);
      setItems((prev) => prev.map((item) => (item.id === updated.id ? updated : item)));
      if (drawerItem && drawerItem.id === updated.id) {
        setDrawerItem(updated);
      }
    } catch (err) {
      console.warn('Failed to refresh item metrics:', err);
    }
  };

  // Create / Edit Subject
  const handleCreateOrUpdateItem = async (payload) => {
    setIsSubmitting(true);
    try {
      if (editingItem) {
        const updated = await learningService.updateLearningItem(editingItem.id, payload);
        setItems((prev) => prev.map((i) => (i.id === updated.id ? { ...i, ...updated } : i)));
        showToast('Learning subject updated.');
      } else {
        const created = await learningService.createLearningItem(payload);
        setItems((prev) => [created, ...prev]);
        showToast('Learning subject created.');
      }
      setIsItemModalOpen(false);
      setEditingItem(null);
    } catch (err) {
      alert(err.message || 'Failed to save learning subject.');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Progress Update (Explicit user-control, does NOT change status!)
  const handleProgressChange = async (item, newProgress) => {
    const clamped = Math.min(100, Math.max(0, newProgress));
    try {
      const updated = await learningService.updateLearningProgress(item.id, clamped);
      setItems((prev) => prev.map((i) => (i.id === updated.id ? { ...i, currentProgress: updated.currentProgress } : i)));
      showToast(`Progress updated to ${clamped}%.`);
    } catch (err) {
      showToast(err.message || 'Failed to update progress.');
    }
  };

  // Status Lifecycle Transition
  const handleStatusChange = async (item, nextStatus) => {
    try {
      const updated = await learningService.updateLearningStatus(item.id, nextStatus);
      setItems((prev) => prev.map((i) => (i.id === updated.id ? { ...i, status: updated.status } : i)));
      const msg = nextStatus === 'PAUSED'
        ? 'Subject paused.'
        : nextStatus === 'COMPLETED'
        ? 'Subject marked completed!'
        : nextStatus === 'ARCHIVED'
        ? 'Subject archived.'
        : 'Subject reactivated.';
      showToast(msg);
    } catch (err) {
      alert(err.message || 'Cannot transition status.');
    }
  };

  // Delete Subject
  const handleDeleteItem = async (item) => {
    if (!window.confirm(`Permanently delete "${item.title}" and all its study sessions? This cannot be undone.`)) {
      return;
    }
    try {
      await learningService.deleteLearningItem(item.id);
      setItems((prev) => prev.filter((i) => i.id !== item.id));
      if (drawerItem && drawerItem.id === item.id) {
        setIsDrawerOpen(false);
        setDrawerItem(null);
      }
      showToast('Subject deleted.');
    } catch (err) {
      alert(err.message || 'Failed to delete subject.');
    }
  };

  // Log Practice Session
  const handleOpenSessionModal = (item) => {
    setSessionTargetItem(item);
    setIsSessionModalOpen(true);
  };

  const handleLogSession = async (payload) => {
    if (!sessionTargetItem) return;
    setIsSubmitting(true);
    try {
      const newSession = await learningService.createLearningSession(sessionTargetItem.id, payload);
      showToast(`Logged ${payload.durationMinutes}m session: "${payload.topic}"`);
      setIsSessionModalOpen(false);

      // Refresh item to update derived metrics
      await refreshItem(sessionTargetItem.id);

      // If drawer is open for this item, add session
      if (isDrawerOpen && drawerItem && drawerItem.id === sessionTargetItem.id) {
        setDrawerSessions((prev) => [newSession, ...prev]);
      }
    } catch (err) {
      alert(err.message || 'Failed to log session.');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Session Drawer Handlers
  const handleOpenDrawer = async (item) => {
    setDrawerItem(item);
    setIsDrawerOpen(true);
    setIsDrawerLoading(true);
    try {
      const sessions = await learningService.getLearningSessions(item.id);
      setDrawerSessions(sessions || []);
    } catch (err) {
      showToast('Failed to load sessions history.');
    } finally {
      setIsDrawerLoading(false);
    }
  };

  const handleDeleteSession = async (sessionId) => {
    if (!drawerItem) return;
    if (!window.confirm('Delete this recorded practice session?')) return;
    try {
      await learningService.deleteLearningSession(drawerItem.id, sessionId);
      setDrawerSessions((prev) => prev.filter((s) => s.id !== sessionId));
      await refreshItem(drawerItem.id);
      showToast('Session deleted.');
    } catch (err) {
      alert(err.message || 'Failed to delete session.');
    }
  };

  return (
    <div className="lifeos-learning-page">
      {toastMessage && (
        <div className="lifeos-feedback-toast" role="status">
          {toastMessage}
        </div>
      )}

      {/* Header Section */}
      <header className="lifeos-learning-header">
        <div className="lifeos-learning-header__titles">
          <span className="lifeos-kicker">GROWTH &amp; MASTERY</span>
          <h1 className="lifeos-learning-header__title">Learning Tracker</h1>
          <p className="lifeos-learning-header__subtitle">
            Cultivate the skills that expand your capabilities
          </p>
        </div>
        <div className="lifeos-learning-header__actions">
          <Button
            variant="primary"
            onClick={() => {
              setEditingItem(null);
              setIsItemModalOpen(true);
            }}
            id="new-learning-subject-btn"
          >
            + New Subject
          </Button>
        </div>
      </header>

      {/* KPI Summary Strip */}
      <section className="lifeos-learning-summary-strip" aria-label="Learning Summary">
        <div className="lifeos-learning-summary-card">
          <span className="lifeos-learning-summary-card__label">Active Subjects</span>
          <strong className="lifeos-learning-summary-card__value">{summary.activeCount}</strong>
        </div>
        <div className="lifeos-learning-summary-card">
          <span className="lifeos-learning-summary-card__label">Total Hours Invested</span>
          <strong className="lifeos-learning-summary-card__value">{summary.totalHours} hrs</strong>
        </div>
        <div className="lifeos-learning-summary-card">
          <span className="lifeos-learning-summary-card__label">Average Progress</span>
          <strong className="lifeos-learning-summary-card__value">{summary.avgProgress}%</strong>
        </div>
      </section>

      {/* Filter Tabs */}
      <nav className="lifeos-learning-nav" aria-label="Learning status filters">
        <div className="lifeos-learning-tabs">
          {FILTER_TABS.map((tab) => {
            const count = items.filter((i) => i.status === tab.key).length;
            return (
              <button
                key={tab.key}
                type="button"
                id={`learning-tab-${tab.key.toLowerCase()}`}
                className={`lifeos-learning-tab ${activeTab === tab.key ? 'lifeos-learning-tab--active' : ''}`}
                onClick={() => setActiveTab(tab.key)}
              >
                {tab.label}
                <span className="lifeos-learning-tab__badge">{count}</span>
              </button>
            );
          })}
        </div>
      </nav>

      {/* Main Content Area */}
      {isLoading ? (
        <div className="lifeos-learning-grid" aria-busy="true">
          <CardSkeleton />
          <CardSkeleton />
          <CardSkeleton />
        </div>
      ) : error ? (
        <div className="lifeos-learning-error" role="alert">
          <p>{error}</p>
          <Button variant="secondary" onClick={fetchItems}>
            Retry
          </Button>
        </div>
      ) : filteredItems.length === 0 ? (
        <EmptyState
          title={
            activeTab === 'ACTIVE'
              ? 'No active subjects yet'
              : activeTab === 'COMPLETED'
              ? 'No completed subjects'
              : activeTab === 'PAUSED'
              ? 'No paused subjects'
              : 'No archived subjects'
          }
          description={
            activeTab === 'ACTIVE'
              ? 'Initiate a learning track to build mastery in languages, engineering, or personal philosophy.'
              : activeTab === 'COMPLETED'
              ? 'Skills you have conquered and mastered will be archived here for lifelong reference.'
              : activeTab === 'PAUSED'
              ? 'Temporarily paused subjects retain your practice logs and progress until you resume.'
              : 'Archived tracks preserve your time invested without cluttering your active radar.'
          }
          actionLabel={activeTab === 'ACTIVE' ? '+ Create Your First Subject' : undefined}
          onAction={
            activeTab === 'ACTIVE'
              ? () => {
                  setEditingItem(null);
                  setIsItemModalOpen(true);
                }
              : undefined
          }
        />
      ) : (
        <div className="lifeos-learning-grid" role="list">
          {filteredItems.map((item) => {
            const progressPercent = Math.min(100, Math.round((item.currentProgress / (item.targetProgress || 100)) * 100));

            return (
              <article key={item.id} className="lifeos-learning-card" role="listitem">
                <header className="lifeos-learning-card__header">
                  <div className="lifeos-learning-card__identity">
                    <div className="lifeos-learning-card__meta-tags">
                      <span className="lifeos-learning-card__tag">
                        {CATEGORY_ICONS[item.category] || '📖'} {item.category}
                      </span>
                      {item.goalTitle && (
                        <span className="lifeos-learning-card__tag lifeos-learning-card__tag--goal">
                          🎯 {item.goalTitle}
                        </span>
                      )}
                      {item.status === 'PAUSED' && (
                        <span className="lifeos-learning-card__tag lifeos-learning-card__tag--status-paused">
                          Paused
                        </span>
                      )}
                      {item.status === 'COMPLETED' && (
                        <span className="lifeos-learning-card__tag lifeos-learning-card__tag--status-completed">
                          Completed
                        </span>
                      )}
                      {item.status === 'ARCHIVED' && (
                        <span className="lifeos-learning-card__tag lifeos-learning-card__tag--status-archived">
                          Archived
                        </span>
                      )}
                    </div>
                    <h2 className="lifeos-learning-card__title">{item.title}</h2>
                  </div>
                </header>

                {item.description && (
                  <p className="lifeos-learning-card__desc">{item.description}</p>
                )}

                {/* Progress Control Section */}
                <div className="lifeos-learning-card__progress-section">
                  <div className="lifeos-learning-card__progress-header">
                    <span className="lifeos-learning-card__progress-label">Syllabus Progress</span>
                    <span className="lifeos-learning-card__progress-values">
                      {item.currentProgress}% / {item.targetProgress}%
                    </span>
                  </div>
                  <div className="lifeos-learning-card__progress-bar-container">
                    <div
                      className="lifeos-learning-card__progress-bar-fill"
                      style={{ width: `${progressPercent}%` }}
                    />
                  </div>

                  {item.status !== 'ARCHIVED' && (
                    <div className="lifeos-learning-card__progress-controls">
                      <button
                        type="button"
                        className="lifeos-learning-card__progress-btn"
                        onClick={() => handleProgressChange(item, item.currentProgress - 5)}
                        title="Decrease progress by 5%"
                        disabled={item.currentProgress <= 0}
                      >
                        -5%
                      </button>
                      <input
                        type="range"
                        min="0"
                        max="100"
                        value={item.currentProgress}
                        onChange={(e) => handleProgressChange(item, Number(e.target.value))}
                        className="lifeos-learning-card__slider"
                        aria-label={`Progress slider for ${item.title}`}
                      />
                      <button
                        type="button"
                        className="lifeos-learning-card__progress-btn"
                        onClick={() => handleProgressChange(item, item.currentProgress + 5)}
                        title="Increase progress by 5%"
                        disabled={item.currentProgress >= 100}
                      >
                        +5%
                      </button>
                    </div>
                  )}
                </div>

                {/* Stats Row */}
                <div className="lifeos-learning-card__stats-row">
                  <span className="lifeos-learning-card__stat-item">
                    Invested: <strong>{item.totalHoursLearned || 0} hrs</strong> ({item.totalMinutesLearned || 0}m)
                  </span>
                  <span className="lifeos-learning-card__stat-item">
                    Sessions: <strong>{item.sessionCount || 0}</strong>
                  </span>
                </div>

                {/* Card Actions */}
                <footer className="lifeos-learning-card__footer">
                  <div className="lifeos-learning-card__actions-left">
                    {item.status !== 'ARCHIVED' && (
                      <Button
                        variant="secondary"
                        size="sm"
                        onClick={() => handleOpenSessionModal(item)}
                      >
                        + Log Session
                      </Button>
                    )}
                    <button
                      type="button"
                      className="lifeos-learning-card__action-btn"
                      onClick={() => handleOpenDrawer(item)}
                    >
                      History ({item.sessionCount || 0})
                    </button>
                  </div>

                  <div className="lifeos-learning-card__actions-right">
                    <button
                      type="button"
                      className="lifeos-learning-card__action-btn"
                      onClick={() => {
                        setEditingItem(item);
                        setIsItemModalOpen(true);
                      }}
                    >
                      Edit
                    </button>

                    {/* Status Lifecycle Controls */}
                    {item.status === 'ACTIVE' && (
                      <>
                        <button
                          type="button"
                          className="lifeos-learning-card__action-btn"
                          onClick={() => handleStatusChange(item, 'PAUSED')}
                        >
                          Pause
                        </button>
                        <button
                          type="button"
                          className="lifeos-learning-card__action-btn"
                          onClick={() => handleStatusChange(item, 'COMPLETED')}
                        >
                          Complete
                        </button>
                        <button
                          type="button"
                          className="lifeos-learning-card__action-btn"
                          onClick={() => handleStatusChange(item, 'ARCHIVED')}
                        >
                          Archive
                        </button>
                      </>
                    )}

                    {item.status === 'PAUSED' && (
                      <>
                        <button
                          type="button"
                          className="lifeos-learning-card__action-btn"
                          onClick={() => handleStatusChange(item, 'ACTIVE')}
                        >
                          Resume
                        </button>
                        <button
                          type="button"
                          className="lifeos-learning-card__action-btn"
                          onClick={() => handleStatusChange(item, 'ARCHIVED')}
                        >
                          Archive
                        </button>
                      </>
                    )}

                    {item.status === 'COMPLETED' && (
                      <>
                        <button
                          type="button"
                          className="lifeos-learning-card__action-btn"
                          onClick={() => handleStatusChange(item, 'ACTIVE')}
                        >
                          Reactivate
                        </button>
                        <button
                          type="button"
                          className="lifeos-learning-card__action-btn"
                          onClick={() => handleStatusChange(item, 'ARCHIVED')}
                        >
                          Archive
                        </button>
                      </>
                    )}

                    {item.status === 'ARCHIVED' && (
                      <button
                        type="button"
                        className="lifeos-learning-card__action-btn"
                        onClick={() => handleStatusChange(item, 'ACTIVE')}
                      >
                        Reactivate
                      </button>
                    )}

                    <button
                      type="button"
                      className="lifeos-learning-card__action-btn lifeos-learning-card__action-btn--delete"
                      onClick={() => handleDeleteItem(item)}
                    >
                      Delete
                    </button>
                  </div>
                </footer>
              </article>
            );
          })}
        </div>
      )}

      {/* Create / Edit Subject Modal */}
      <LearningModal
        isOpen={isItemModalOpen}
        onClose={() => {
          setIsItemModalOpen(false);
          setEditingItem(null);
        }}
        onSubmit={handleCreateOrUpdateItem}
        learningItem={editingItem}
        isSubmitting={isSubmitting}
      />

      {/* Log Practice Session Modal */}
      <LearningSessionModal
        isOpen={isSessionModalOpen}
        onClose={() => {
          setIsSessionModalOpen(false);
          setSessionTargetItem(null);
        }}
        onSubmit={handleLogSession}
        learningItem={sessionTargetItem}
        isSubmitting={isSubmitting}
      />

      {/* Practice Sessions Drawer */}
      <LearningSessionsDrawer
        isOpen={isDrawerOpen}
        onClose={() => {
          setIsDrawerOpen(false);
          setDrawerItem(null);
        }}
        learningItem={drawerItem}
        sessions={drawerSessions}
        isLoading={isDrawerLoading}
        onLogSession={(item) => {
          setIsDrawerOpen(false);
          handleOpenSessionModal(item);
        }}
        onDeleteSession={handleDeleteSession}
      />
    </div>
  );
};

export default LearningPage;
