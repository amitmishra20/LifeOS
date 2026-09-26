import React, { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import Input from '../ui/Input';
import Select from '../ui/Select';
import Textarea from '../ui/Textarea';
import goalService from '../../services/goalService';
import milestoneService from '../../services/milestoneService';
import './TaskModal.css';

const PRIORITY_OPTIONS = [
  { value: 'LOW', label: 'Low Priority' },
  { value: 'MEDIUM', label: 'Medium Priority' },
  { value: 'HIGH', label: 'High Priority' },
  { value: 'CRITICAL', label: 'Critical Priority' },
];

export const TaskModal = ({
  isOpen,
  onClose,
  onSubmit,
  task = null,
  isSubmitting = false,
}) => {
  const isEdit = Boolean(task && task.id);

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    priority: 'MEDIUM',
    dueDate: '',
    estimatedMinutes: '',
    goalId: '',
    milestoneId: '',
  });

  const [goals, setGoals] = useState([]);
  const [milestones, setMilestones] = useState([]);
  const [isLoadingMilestones, setIsLoadingMilestones] = useState(false);
  const [errors, setErrors] = useState({});

  // Fetch available goals when modal opens
  useEffect(() => {
    if (!isOpen) return;

    let isMounted = true;
    const loadGoals = async () => {
      try {
        const goalsList = await goalService.getGoals();
        if (isMounted) {
          setGoals(goalsList || []);
        }
      } catch (err) {
        console.error('Failed to load goals for task modal', err);
      }
    };

    loadGoals();
    return () => {
      isMounted = false;
    };
  }, [isOpen]);

  // Fetch milestones when goalId changes
  useEffect(() => {
    if (!formData.goalId) {
      setMilestones([]);
      return;
    }

    let isMounted = true;
    const loadMilestones = async () => {
      setIsLoadingMilestones(true);
      try {
        const milestonesList = await milestoneService.getMilestonesByGoal(formData.goalId);
        if (isMounted) {
          setMilestones(milestonesList || []);
        }
      } catch (err) {
        console.error('Failed to load milestones for task modal', err);
      } finally {
        if (isMounted) {
          setIsLoadingMilestones(false);
        }
      }
    };

    loadMilestones();
    return () => {
      isMounted = false;
    };
  }, [formData.goalId]);

  // Sync form data on task change or modal open
  useEffect(() => {
    if (task) {
      setFormData({
        title: task.title || '',
        description: task.description || '',
        priority: task.priority || 'MEDIUM',
        dueDate: task.dueDate || '',
        estimatedMinutes: task.estimatedMinutes != null ? String(task.estimatedMinutes) : '',
        goalId: task.goalId ? String(task.goalId) : '',
        milestoneId: task.milestoneId ? String(task.milestoneId) : '',
      });
    } else {
      setFormData({
        title: '',
        description: '',
        priority: 'MEDIUM',
        dueDate: '',
        estimatedMinutes: '',
        goalId: '',
        milestoneId: '',
      });
    }
    setErrors({});
  }, [task, isOpen]);

  const handleChange = (field, value) => {
    setFormData((prev) => {
      const next = { ...prev, [field]: value };
      if (field === 'goalId') {
        // Reset milestone when goal changes
        next.milestoneId = '';
      }
      return next;
    });

    if (errors[field]) {
      setErrors((prev) => {
        const next = { ...prev };
        delete next[field];
        return next;
      });
    }
  };

  const validate = () => {
    const newErrors = {};
    if (!formData.title || !formData.title.trim()) {
      newErrors.title = 'Title is required';
    } else if (formData.title.trim().length > 255) {
      newErrors.title = 'Title cannot exceed 255 characters';
    }

    if (formData.estimatedMinutes && isNaN(Number(formData.estimatedMinutes))) {
      newErrors.estimatedMinutes = 'Must be a valid number of minutes';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      title: formData.title.trim(),
      description: formData.description.trim() || null,
      priority: formData.priority,
      dueDate: formData.dueDate || null,
      estimatedMinutes: formData.estimatedMinutes ? Number(formData.estimatedMinutes) : null,
      goalId: formData.goalId ? Number(formData.goalId) : null,
      milestoneId: formData.milestoneId ? Number(formData.milestoneId) : null,
    };

    onSubmit(payload);
  };

  const goalOptions = [
    { value: '', label: 'None (Standalone Task)' },
    ...goals.map((g) => ({ value: String(g.id), label: g.title })),
  ];

  const milestoneOptions = [
    { value: '', label: 'None (Direct to Goal)' },
    ...milestones.map((m) => ({ value: String(m.id), label: m.title })),
  ];

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={isEdit ? 'Edit Task' : 'New Task'}
      subtitle={isEdit ? 'Update details, priority, and horizon' : 'Capture an action with clear intent and placement'}
    >
      <form onSubmit={handleSubmit} className="lifeos-task-form">
        <Input
          id="task-title"
          label="Title"
          placeholder="e.g. Draft launch sequence overview"
          value={formData.title}
          onChange={(e) => handleChange('title', e.target.value)}
          error={errors.title}
          required
          autoFocus
        />

        <Textarea
          id="task-description"
          label="Context & Description"
          placeholder="Any notes, references, or specific criteria..."
          value={formData.description}
          onChange={(e) => handleChange('description', e.target.value)}
          rows={3}
        />

        <div className="lifeos-task-form__grid">
          <Select
            id="task-priority"
            label="Priority"
            options={PRIORITY_OPTIONS}
            value={formData.priority}
            onChange={(e) => handleChange('priority', e.target.value)}
          />

          <Input
            id="task-due-date"
            label="Due Date"
            type="date"
            value={formData.dueDate}
            onChange={(e) => handleChange('dueDate', e.target.value)}
          />
        </div>

        <div className="lifeos-task-form__grid">
          <Input
            id="task-estimated-minutes"
            label="Est. Duration (Minutes)"
            type="number"
            placeholder="e.g. 45"
            min="0"
            value={formData.estimatedMinutes}
            onChange={(e) => handleChange('estimatedMinutes', e.target.value)}
            error={errors.estimatedMinutes}
          />

          <Select
            id="task-goal"
            label="Linked Strategic Goal"
            options={goalOptions}
            value={formData.goalId}
            onChange={(e) => handleChange('goalId', e.target.value)}
          />
        </div>

        {formData.goalId && (
          <Select
            id="task-milestone"
            label={isLoadingMilestones ? 'Loading Milestones...' : 'Linked Milestone'}
            options={milestoneOptions}
            value={formData.milestoneId}
            onChange={(e) => handleChange('milestoneId', e.target.value)}
            disabled={isLoadingMilestones || milestones.length === 0}
          />
        )}

        <div className="lifeos-modal__actions">
          <Button
            type="button"
            variant="ghost"
            onClick={onClose}
            disabled={isSubmitting}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            variant="primary"
            isLoading={isSubmitting}
          >
            {isEdit ? 'Save Changes' : 'Create Task'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default TaskModal;
