import React, { useState, useEffect, useCallback, useMemo } from 'react';
import goalHealthService from '../services/goalHealthService';
import GoalHealthSummaryCard from '../components/goalhealth/GoalHealthSummaryCard';
import GoalHealthItemCard from '../components/goalhealth/GoalHealthItemCard';
import LoadingState from '../components/ui/LoadingState';
import EmptyState from '../components/ui/EmptyState';
import Button from '../components/ui/Button';
import Select from '../components/ui/Select';
import { IconGoals, IconGoalHealth } from '../components/ui/Icons';
import './GoalHealthPage.css';

const CATEGORY_OPTIONS = [
  { value: 'ALL', label: 'All Categories' },
  { value: 'CAREER', label: 'Career' },
  { value: 'HEALTH', label: 'Health' },
  { value: 'PERSONAL', label: 'Personal' },
  { value: 'FINANCE', label: 'Finance' },
  { value: 'EDUCATION', label: 'Education' },
  { value: 'OTHER', label: 'Other' },
];

const SORT_OPTIONS = [
  { value: 'DELTA_ASC', label: 'Schedule Gap (Most Behind)' },
  { value: 'DEADLINE_ASC', label: 'Deadline (Earliest First)' },
  { value: 'PROGRESS_DESC', label: 'Actual Progress (Highest First)' },
  { value: 'PROGRESS_ASC', label: 'Actual Progress (Lowest First)' },
];

export const GoalHealthPage = () => {
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  // Filters & Sorting
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [categoryFilter, setCategoryFilter] = useState('ALL');
  const [sortBy, setSortBy] = useState('DELTA_ASC');
  const [searchQuery, setSearchQuery] = useState('');

  const fetchOverview = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      const res = await goalHealthService.getGoalHealthOverview();
      setData(res);
    } catch (err) {
      console.error('Failed to load goal health overview:', err);
      setError(err.response?.data?.message || err.message || 'Failed to load goal health data');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchOverview();
  }, [fetchOverview]);

  const filteredEvaluations = useMemo(() => {
    if (!data || !data.evaluations) return [];

    return data.evaluations
      .filter((item) => {
        // Status Filter
        if (statusFilter !== 'ALL' && item.health !== statusFilter) {
          return false;
        }
        // Category Filter
        if (categoryFilter !== 'ALL' && item.category !== categoryFilter) {
          return false;
        }
        // Search Query
        if (searchQuery.trim()) {
          const q = searchQuery.toLowerCase();
          const matchTitle = item.goalTitle?.toLowerCase().includes(q);
          const matchReason = item.reason?.toLowerCase().includes(q);
          if (!matchTitle && !matchReason) return false;
        }
        return true;
      })
      .sort((a, b) => {
        if (sortBy === 'DELTA_ASC') {
          return (a.delta ?? 0) - (b.delta ?? 0);
        }
        if (sortBy === 'DEADLINE_ASC') {
          if (!a.targetDate && !b.targetDate) return 0;
          if (!a.targetDate) return 1;
          if (!b.targetDate) return -1;
          return new Date(a.targetDate) - new Date(b.targetDate);
        }
        if (sortBy === 'PROGRESS_DESC') {
          return (b.actualProgress ?? 0) - (a.actualProgress ?? 0);
        }
        if (sortBy === 'PROGRESS_ASC') {
          return (a.actualProgress ?? 0) - (b.actualProgress ?? 0);
        }
        return 0;
      });
  }, [data, statusFilter, categoryFilter, sortBy, searchQuery]);

  if (isLoading) {
    return (
      <div className="lifeos-gh-page">
        <LoadingState message="Evaluating goal health and trajectory..." />
      </div>
    );
  }

  if (error) {
    return (
      <div className="lifeos-gh-page">
        <div className="lifeos-gh-page__error" role="alert">
          <h2>Unable to load goal health</h2>
          <p>{error}</p>
          <Button variant="secondary" onClick={fetchOverview}>
            Try Again
          </Button>
        </div>
      </div>
    );
  }

  const {
    totalGoals = 0,
    onTrackCount = 0,
    atRiskCount = 0,
    behindCount = 0,
    completedCount = 0,
  } = data || {};

  return (
    <div className="lifeos-gh-page">
      {/* Header */}
      <header className="lifeos-gh-page__header">
        <div className="lifeos-gh-page__title-group">
          <div className="lifeos-gh-page__icon">
            <IconGoalHealth />
          </div>
          <div>
            <h1 className="lifeos-gh-page__title">Goal Health & Schedule Trajectory</h1>
            <p className="lifeos-gh-page__subtitle">
              Deterministic, explainable health evaluations comparing actual milestone completion against expected timeline pace.
            </p>
          </div>
        </div>
      </header>

      {/* Summary Stat Cards (Also act as status filter buttons) */}
      <GoalHealthSummaryCard
        totalGoals={totalGoals}
        onTrackCount={onTrackCount}
        atRiskCount={atRiskCount}
        behindCount={behindCount}
        completedCount={completedCount}
        selectedFilter={statusFilter}
        onSelectFilter={setStatusFilter}
      />

      {/* Controls Bar */}
      <div className="lifeos-gh-page__controls">
        <div className="lifeos-gh-page__search-wrap">
          <input
            type="text"
            className="lifeos-gh-page__search-input"
            placeholder="Search goals by title or reason..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            aria-label="Search goals"
          />
          {searchQuery && (
            <button
              type="button"
              className="lifeos-gh-page__search-clear"
              onClick={() => setSearchQuery('')}
              aria-label="Clear search"
            >
              ×
            </button>
          )}
        </div>

        <div className="lifeos-gh-page__dropdowns">
          <div className="lifeos-gh-page__select-group">
            <Select
              options={CATEGORY_OPTIONS}
              value={categoryFilter}
              onChange={(e) => setCategoryFilter(e.target.value)}
              aria-label="Filter by category"
            />
          </div>

          <div className="lifeos-gh-page__select-group">
            <Select
              options={SORT_OPTIONS}
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              aria-label="Sort goals"
            />
          </div>
        </div>
      </div>

      {/* Goal Cards Grid */}
      {totalGoals === 0 ? (
        <EmptyState
          icon={<IconGoals />}
          title="No Goals Found"
          description="Create your first strategic goal in the Goals module to start tracking its health and milestone trajectory."
        />
      ) : filteredEvaluations.length === 0 ? (
        <EmptyState
          title="No Matching Goals"
          description="No goals match your current filter and search criteria. Try adjusting your filters."
          actionText="Reset Filters"
          onAction={() => {
            setStatusFilter('ALL');
            setCategoryFilter('ALL');
            setSearchQuery('');
          }}
        />
      ) : (
        <div className="lifeos-gh-page__grid">
          {filteredEvaluations.map((item) => (
            <GoalHealthItemCard key={item.goalId} evaluation={item} />
          ))}
        </div>
      )}
    </div>
  );
};

export default GoalHealthPage;
