import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import goalService from '../services/goalService';
import milestoneService from '../services/milestoneService';
import LifeMap from '../components/lifemap/LifeMap';
import MilestoneList from '../components/milestones/MilestoneList';
import MilestoneModal from '../components/milestones/MilestoneModal';
import GoalModal from '../components/goals/GoalModal';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';
import ProgressBar from '../components/ui/ProgressBar';
import LoadingState from '../components/ui/LoadingState';
import Modal from '../components/ui/Modal';
import Select from '../components/ui/Select';
import './GoalDetailPage.css';

const CATEGORY_LABELS = {
  CAREER: 'Career & Work',
  HEALTH: 'Health & Vitality',
  PERSONAL: 'Personal Growth',
  FINANCE: 'Finance & Wealth',
  EDUCATION: 'Education & Study',
  OTHER: 'Other',
};

const CATEGORY_BADGES = {
  CAREER: 'accent',
  HEALTH: 'emerald',
  PERSONAL: 'neutral',
  FINANCE: 'warm',
  EDUCATION: 'default',
  OTHER: 'neutral',
};

const PRIORITY_BADGES = {
  LOW: 'neutral',
  MEDIUM: 'default',
  HIGH: 'warm',
  CRITICAL: 'crimson',
};

const STATUS_OPTIONS = [
  { value: 'ACTIVE', label: 'Active' },
  { value: 'PAUSED', label: 'Paused' },
  { value: 'COMPLETED', label: 'Completed' },
  { value: 'ARCHIVED', label: 'Archived' },
];

export const GoalDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [goal, setGoal] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  // Goal Edit / Delete Modals
  const [isEditGoalModalOpen, setIsEditGoalModalOpen] = useState(false);
  const [isDeleteGoalModalOpen, setIsDeleteGoalModalOpen] = useState(false);
  const [isSubmittingGoal, setIsSubmittingGoal] = useState(false);

  // Milestone Create / Edit / Delete Modals
  const [isMilestoneModalOpen, setIsMilestoneModalOpen] = useState(false);
  const [editingMilestone, setEditingMilestone] = useState(null);
  const [deletingMilestone, setDeletingMilestone] = useState(null);
  const [isSubmittingMilestone, setIsSubmittingMilestone] = useState(false);

  const fetchGoal = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await goalService.getGoalById(id);
      setGoal(data);
    } catch (err) {
      setError(err.message || 'Failed to load goal.');
    } finally {
      setIsLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchGoal();
  }, [fetchGoal]);

  // Goal Actions
  const handleUpdateGoal = async (payload) => {
    setIsSubmittingGoal(true);
    try {
      const updated = await goalService.updateGoal(goal.id, payload);
      setGoal((prev) => ({ ...prev, ...updated }));
      setIsEditGoalModalOpen(false);
    } catch (err) {
      alert(err.message || 'Failed to update goal');
    } finally {
      setIsSubmittingGoal(false);
    }
  };

  const handleStatusChange = async (newStatus) => {
    try {
      const payload = {
        title: goal.title,
        description: goal.description,
        category: goal.category,
        priority: goal.priority,
        status: newStatus,
        startDate: goal.startDate,
        targetDate: goal.targetDate,
      };
      const updated = await goalService.updateGoal(goal.id, payload);
      setGoal((prev) => ({ ...prev, status: updated.status }));
    } catch (err) {
      alert(err.message || 'Failed to update goal status');
    }
  };

  const handleDeleteGoal = async () => {
    setIsSubmittingGoal(true);
    try {
      await goalService.deleteGoal(goal.id);
      navigate('/goals');
    } catch (err) {
      alert(err.message || 'Failed to delete goal');
      setIsSubmittingGoal(false);
    }
  };

  // Milestone Actions
  const handleSaveMilestone = async (payload) => {
    setIsSubmittingMilestone(true);
    try {
      if (editingMilestone) {
        await milestoneService.updateMilestone(editingMilestone.id, payload);
      } else {
        await milestoneService.createMilestone(goal.id, payload);
      }
      setIsMilestoneModalOpen(false);
      setEditingMilestone(null);
      // Re-fetch parent goal so progress & milestones are refreshed
      await fetchGoal();
    } catch (err) {
      alert(err.message || 'Failed to save milestone');
    } finally {
      setIsSubmittingMilestone(false);
    }
  };

  const handleToggleMilestoneStatus = async (milestone) => {
    const nextStatus = milestone.status === 'COMPLETED' ? 'PENDING' : 'COMPLETED';
    try {
      await milestoneService.updateMilestone(milestone.id, {
        title: milestone.title,
        description: milestone.description,
        targetDate: milestone.targetDate,
        status: nextStatus,
        orderIndex: milestone.orderIndex,
      });
      await fetchGoal();
    } catch (err) {
      alert(err.message || 'Failed to update milestone status');
    }
  };

  const handleDeleteMilestone = async () => {
    if (!deletingMilestone) return;
    setIsSubmittingMilestone(true);
    try {
      await milestoneService.deleteMilestone(deletingMilestone.id);
      setDeletingMilestone(null);
      await fetchGoal();
    } catch (err) {
      alert(err.message || 'Failed to delete milestone');
    } finally {
      setIsSubmittingMilestone(false);
    }
  };

  if (isLoading) {
    return <LoadingState message="Loading goal details..." />;
  }

  if (error || !goal) {
    return (
      <div className="lifeos-goal-detail-error">
        <h2 className="lifeos-goal-detail-error__title">Goal Not Found</h2>
        <p className="lifeos-goal-detail-error__desc">
          {error || 'The requested goal could not be found or you do not have permission to view it.'}
        </p>
        <Button variant="secondary" size="md" onClick={() => navigate('/goals')}>
          ← Return to Goals
        </Button>
      </div>
    );
  }

  const milestones = goal.milestones || [];
  const completedCount = milestones.filter((m) => m.status === 'COMPLETED').length;
  const totalCount = milestones.length;

  const startDateFormatted = goal.startDate
    ? new Date(goal.startDate).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
      })
    : null;

  const targetDateFormatted = goal.targetDate
    ? new Date(goal.targetDate).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
      })
    : null;

  return (
    <div className="lifeos-goal-detail">
      {/* Breadcrumb Back Button */}
      <nav className="lifeos-goal-detail__nav">
        <button
          type="button"
          className="lifeos-goal-detail__back-btn"
          onClick={() => navigate('/goals')}
        >
          ← Strategic Goals
        </button>
      </nav>

      {/* Goal Header */}
      <header className="lifeos-goal-detail__header">
        <div className="lifeos-goal-detail__meta-top">
          <div className="lifeos-goal-detail__badges">
            <Badge variant={CATEGORY_BADGES[goal.category] || 'default'} size="md">
              <span>{CATEGORY_LABELS[goal.category] || goal.category}</span>
            </Badge>
            <Badge variant={PRIORITY_BADGES[goal.priority] || 'default'} size="md">
              <span>{goal.priority} Priority</span>
            </Badge>
          </div>

          <div className="lifeos-goal-detail__status-control">
            <span className="lifeos-goal-detail__status-label">Status:</span>
            <div className="lifeos-goal-detail__status-select">
              <Select
                options={STATUS_OPTIONS}
                value={goal.status}
                onChange={(e) => handleStatusChange(e.target.value)}
                aria-label="Change goal status"
              />
            </div>
          </div>
        </div>

        <div className="lifeos-goal-detail__title-section">
          <h1 className="lifeos-goal-detail__title">{goal.title}</h1>
          <div className="lifeos-goal-detail__actions">
            <Button
              variant="secondary"
              size="sm"
              onClick={() => setIsEditGoalModalOpen(true)}
            >
              Edit Goal
            </Button>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setIsDeleteGoalModalOpen(true)}
              className="lifeos-danger-btn"
            >
              Delete
            </Button>
          </div>
        </div>

        {goal.description && (
          <p className="lifeos-goal-detail__description">{goal.description}</p>
        )}

        {/* Timeline & Progress Bar Strip */}
        <div className="lifeos-goal-detail__timeline-strip">
          <div className="lifeos-goal-detail__progress-card">
            <div className="lifeos-goal-detail__progress-header">
              <span className="lifeos-goal-detail__progress-label">Goal Progress</span>
              <span className="lifeos-goal-detail__progress-val">{goal.progress}%</span>
            </div>
            <ProgressBar value={goal.progress} size="md" variant={goal.progress === 100 ? 'emerald' : 'accent'} />
            <span className="lifeos-goal-detail__milestone-ratio">
              {totalCount === 0
                ? 'No milestones defined yet'
                : `${completedCount} of ${totalCount} milestones completed`}
            </span>
          </div>

          <div className="lifeos-goal-detail__dates-card">
            <div className="lifeos-goal-detail__date-item">
              <span className="lifeos-goal-detail__date-label">Start Date</span>
              <span className="lifeos-goal-detail__date-value">
                {startDateFormatted || 'Not set'}
              </span>
            </div>
            <div className="lifeos-goal-detail__date-sep">→</div>
            <div className="lifeos-goal-detail__date-item">
              <span className="lifeos-goal-detail__date-label">Target Arrival</span>
              <span className="lifeos-goal-detail__date-value">
                {targetDateFormatted || 'Open-ended'}
              </span>
            </div>
          </div>
        </div>
      </header>

      {/* Life Map Spatial Section */}
      <section className="lifeos-goal-detail__section" aria-label="Life Map">
        <LifeMap
          goal={goal}
          milestones={milestones}
        />
      </section>

      {/* Phased Milestones Management Section */}
      <section className="lifeos-goal-detail__section" aria-label="Milestones Management">
        <MilestoneList
          milestones={milestones}
          onAddMilestone={() => {
            setEditingMilestone(null);
            setIsMilestoneModalOpen(true);
          }}
          onEditMilestone={(m) => {
            setEditingMilestone(m);
            setIsMilestoneModalOpen(true);
          }}
          onDeleteMilestone={(m) => {
            setDeletingMilestone(m);
          }}
          onToggleStatus={handleToggleMilestoneStatus}
        />
      </section>

      {/* Edit Goal Modal */}
      <GoalModal
        isOpen={isEditGoalModalOpen}
        onClose={() => setIsEditGoalModalOpen(false)}
        onSubmit={handleUpdateGoal}
        goal={goal}
        isSubmitting={isSubmittingGoal}
      />

      {/* Delete Goal Confirmation Modal */}
      <Modal
        isOpen={isDeleteGoalModalOpen}
        onClose={() => setIsDeleteGoalModalOpen(false)}
        title="Delete Strategic Goal"
        description="Are you sure you want to delete this goal? All associated milestones will also be permanently removed. This action cannot be undone."
        footer={
          <div className="lifeos-modal-footer-actions">
            <Button
              variant="ghost"
              size="md"
              onClick={() => setIsDeleteGoalModalOpen(false)}
              disabled={isSubmittingGoal}
            >
              Cancel
            </Button>
            <Button
              variant="danger"
              size="md"
              onClick={handleDeleteGoal}
              loading={isSubmittingGoal}
            >
              Delete Goal
            </Button>
          </div>
        }
      >
        <p style={{ margin: 0, fontSize: 'var(--font-size-sm)', color: 'var(--color-text-secondary)' }}>
          Goal: <strong>{goal.title}</strong> ({milestones.length} milestones)
        </p>
      </Modal>

      {/* Create / Edit Milestone Modal */}
      <MilestoneModal
        isOpen={isMilestoneModalOpen}
        onClose={() => {
          setIsMilestoneModalOpen(false);
          setEditingMilestone(null);
        }}
        onSubmit={handleSaveMilestone}
        milestone={editingMilestone}
        isSubmitting={isSubmittingMilestone}
      />

      {/* Delete Milestone Confirmation Modal */}
      <Modal
        isOpen={Boolean(deletingMilestone)}
        onClose={() => setDeletingMilestone(null)}
        title="Delete Milestone"
        description="Are you sure you want to delete this milestone? Goal progress will be automatically recalculated."
        footer={
          <div className="lifeos-modal-footer-actions">
            <Button
              variant="ghost"
              size="md"
              onClick={() => setDeletingMilestone(null)}
              disabled={isSubmittingMilestone}
            >
              Cancel
            </Button>
            <Button
              variant="danger"
              size="md"
              onClick={handleDeleteMilestone}
              loading={isSubmittingMilestone}
            >
              Delete Milestone
            </Button>
          </div>
        }
      >
        <p style={{ margin: 0, fontSize: 'var(--font-size-sm)', color: 'var(--color-text-secondary)' }}>
          Milestone: <strong>{deletingMilestone?.title}</strong>
        </p>
      </Modal>
    </div>
  );
};

export default GoalDetailPage;
