import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import goalService from '../services/goalService';
import GoalCard from '../components/goals/GoalCard';
import GoalModal from '../components/goals/GoalModal';
import Button from '../components/ui/Button';
import EmptyState from '../components/ui/EmptyState';
import LoadingState from '../components/ui/LoadingState';
import Select from '../components/ui/Select';
import './GoalsPage.css';

const STATUS_FILTERS = [
  { key: 'ALL', label: 'All Goals' },
  { key: 'ACTIVE', label: 'Active' },
  { key: 'PAUSED', label: 'Paused' },
  { key: 'COMPLETED', label: 'Completed' },
  { key: 'ARCHIVED', label: 'Archived' },
];

const CATEGORY_FILTER_OPTIONS = [
  { value: 'ALL', label: 'All Categories' },
  { value: 'CAREER', label: 'Career & Work' },
  { value: 'HEALTH', label: 'Health & Vitality' },
  { value: 'PERSONAL', label: 'Personal Growth' },
  { value: 'FINANCE', label: 'Finance & Wealth' },
  { value: 'EDUCATION', label: 'Education & Study' },
  { value: 'OTHER', label: 'Other' },
];

export const GoalsPage = () => {
  const navigate = useNavigate();

  const [goals, setGoals] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const [statusFilter, setStatusFilter] = useState('ALL');
  const [categoryFilter, setCategoryFilter] = useState('ALL');

  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const fetchGoals = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await goalService.getGoals();
      setGoals(data);
    } catch (err) {
      setError(err.message || 'Failed to load goals.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchGoals();
  }, [fetchGoals]);

  const handleCreateGoal = async (payload) => {
    setIsSubmitting(true);
    try {
      const newGoal = await goalService.createGoal(payload);
      setGoals((prev) => [newGoal, ...prev]);
      setIsCreateModalOpen(false);
    } catch (err) {
      alert(err.message || 'Failed to create goal');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Filter goals client-side for fluid performance
  const filteredGoals = goals.filter((g) => {
    const matchesStatus = statusFilter === 'ALL' || g.status === statusFilter;
    const matchesCategory = categoryFilter === 'ALL' || g.category === categoryFilter;
    return matchesStatus && matchesCategory;
  });

  return (
    <div className="lifeos-goals-page">
      {/* Page Header */}
      <header className="lifeos-goals-page__header">
        <div className="lifeos-goals-page__title-block">
          <span className="lifeos-goals-page__eyebrow">Strategic Foundation</span>
          <h1 className="lifeos-goals-page__title">Strategic Goals</h1>
          <p className="lifeos-goals-page__subline">
            Define the outcomes that direct your daily focus and milestone pacing.
          </p>
        </div>

        <Button
          variant="primary"
          size="md"
          onClick={() => setIsCreateModalOpen(true)}
          className="lifeos-goals-page__create-btn"
        >
          + New Goal
        </Button>
      </header>

      {/* Filter Toolbar */}
      {goals.length > 0 && (
        <div className="lifeos-goals-page__toolbar">
          <div className="lifeos-goals-page__status-tabs" role="tablist" aria-label="Goal status filters">
            {STATUS_FILTERS.map((f) => (
              <button
                key={f.key}
                type="button"
                role="tab"
                aria-selected={statusFilter === f.key}
                className={`lifeos-status-tab ${statusFilter === f.key ? 'lifeos-status-tab--active' : ''}`}
                onClick={() => setStatusFilter(f.key)}
              >
                {f.label}
              </button>
            ))}
          </div>

          <div className="lifeos-goals-page__category-select">
            <Select
              options={CATEGORY_FILTER_OPTIONS}
              value={categoryFilter}
              onChange={(e) => setCategoryFilter(e.target.value)}
              aria-label="Filter by category"
            />
          </div>
        </div>
      )}

      {/* Main Content Area */}
      {isLoading ? (
        <LoadingState message="Loading your strategic goals..." />
      ) : error ? (
        <div className="lifeos-goals-page__error" role="alert">
          <p className="lifeos-goals-page__error-msg">{error}</p>
          <Button variant="secondary" size="sm" onClick={fetchGoals}>
            Try Again
          </Button>
        </div>
      ) : goals.length === 0 ? (
        <div className="lifeos-goals-page__empty">
          <EmptyState
            icon={
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="12" r="10" />
                <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z" />
                <path d="M2 12h20" />
              </svg>
            }
            title="No strategic goals defined yet."
            description="LifeOS connects your daily actions to long-term outcomes. Start by defining your first meaningful goal."
            action={
              <Button variant="primary" size="md" onClick={() => setIsCreateModalOpen(true)}>
                Create Your First Goal
              </Button>
            }
          />
        </div>
      ) : filteredGoals.length === 0 ? (
        <div className="lifeos-goals-page__empty-filter">
          <p>No goals found matching the selected filter criteria.</p>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => {
              setStatusFilter('ALL');
              setCategoryFilter('ALL');
            }}
          >
            Reset Filters
          </Button>
        </div>
      ) : (
        <div className="lifeos-goals-grid">
          {filteredGoals.map((goal) => (
            <GoalCard key={goal.id} goal={goal} onClick={(id) => navigate(`/goals/${id}`)} />
          ))}
        </div>
      )}

      {/* Create Goal Modal */}
      <GoalModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onSubmit={handleCreateGoal}
        isSubmitting={isSubmitting}
      />
    </div>
  );
};

export default GoalsPage;
