import React, { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import Input from '../ui/Input';
import Select from '../ui/Select';
import Textarea from '../ui/Textarea';
import './GoalModal.css';

const CATEGORY_OPTIONS = [
  { value: 'CAREER', label: 'Career & Work' },
  { value: 'HEALTH', label: 'Health & Vitality' },
  { value: 'PERSONAL', label: 'Personal Growth' },
  { value: 'FINANCE', label: 'Finance & Wealth' },
  { value: 'EDUCATION', label: 'Education & Study' },
  { value: 'OTHER', label: 'Other' },
];

const PRIORITY_OPTIONS = [
  { value: 'LOW', label: 'Low' },
  { value: 'MEDIUM', label: 'Medium' },
  { value: 'HIGH', label: 'High' },
  { value: 'CRITICAL', label: 'Critical' },
];

const STATUS_OPTIONS = [
  { value: 'ACTIVE', label: 'Active' },
  { value: 'PAUSED', label: 'Paused' },
  { value: 'COMPLETED', label: 'Completed' },
  { value: 'ARCHIVED', label: 'Archived' },
];

export const GoalModal = ({
  isOpen,
  onClose,
  onSubmit,
  goal = null,
  isSubmitting = false,
}) => {
  const isEdit = Boolean(goal && goal.id);

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: 'CAREER',
    priority: 'MEDIUM',
    status: 'ACTIVE',
    startDate: '',
    targetDate: '',
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (goal) {
      setFormData({
        title: goal.title || '',
        description: goal.description || '',
        category: goal.category || 'CAREER',
        priority: goal.priority || 'MEDIUM',
        status: goal.status || 'ACTIVE',
        startDate: goal.startDate || '',
        targetDate: goal.targetDate || '',
      });
    } else {
      setFormData({
        title: '',
        description: '',
        category: 'CAREER',
        priority: 'MEDIUM',
        status: 'ACTIVE',
        startDate: '',
        targetDate: '',
      });
    }
    setErrors({});
  }, [goal, isOpen]);

  const handleChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: null }));
    }
  };

  const validate = () => {
    const errs = {};
    if (!formData.title.trim()) {
      errs.title = 'Title is required';
    } else if (formData.title.length > 255) {
      errs.title = 'Title cannot exceed 255 characters';
    }

    if (formData.description && formData.description.length > 2000) {
      errs.description = 'Description cannot exceed 2000 characters';
    }

    if (formData.startDate && formData.targetDate) {
      if (new Date(formData.targetDate) < new Date(formData.startDate)) {
        errs.targetDate = 'Target date must be on or after start date';
      }
    }

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      title: formData.title.trim(),
      description: formData.description.trim() || null,
      category: formData.category,
      priority: formData.priority,
      startDate: formData.startDate || null,
      targetDate: formData.targetDate || null,
    };

    if (isEdit) {
      payload.status = formData.status;
    }

    onSubmit(payload);
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={isEdit ? 'Edit Strategic Goal' : 'Define Strategic Goal'}
      description={
        isEdit
          ? 'Update the direction, priority, or timeline for this outcome.'
          : 'Define a high-level outcome that anchors your milestones and actions.'
      }
      footer={
        <div className="lifeos-modal-footer-actions">
          <Button variant="ghost" size="md" onClick={onClose} disabled={isSubmitting}>
            Cancel
          </Button>
          <Button variant="primary" size="md" onClick={handleSubmit} loading={isSubmitting}>
            {isEdit ? 'Save Changes' : 'Create Goal'}
          </Button>
        </div>
      }
    >
      <form onSubmit={handleSubmit} className="lifeos-goal-form">
        <Input
          label="Goal Title"
          placeholder="e.g., Software Engineering Placement"
          value={formData.title}
          onChange={(e) => handleChange('title', e.target.value)}
          error={errors.title}
          autoFocus
          required
        />

        <div className="lifeos-goal-form__grid">
          <Select
            label="Category"
            options={CATEGORY_OPTIONS}
            value={formData.category}
            onChange={(e) => handleChange('category', e.target.value)}
          />

          <Select
            label="Priority"
            options={PRIORITY_OPTIONS}
            value={formData.priority}
            onChange={(e) => handleChange('priority', e.target.value)}
          />
        </div>

        {isEdit && (
          <Select
            label="Lifecycle Status"
            options={STATUS_OPTIONS}
            value={formData.status}
            onChange={(e) => handleChange('status', e.target.value)}
            hint="Goal status is explicitly controlled by you."
          />
        )}

        <div className="lifeos-goal-form__grid">
          <Input
            type="date"
            label="Start Date"
            value={formData.startDate}
            onChange={(e) => handleChange('startDate', e.target.value)}
          />

          <Input
            type="date"
            label="Target Date"
            value={formData.targetDate}
            onChange={(e) => handleChange('targetDate', e.target.value)}
            error={errors.targetDate}
          />
        </div>

        <Textarea
          label="Outcome Narrative / Notes"
          placeholder="What does arriving at this goal mean? What will be different?"
          value={formData.description}
          onChange={(e) => handleChange('description', e.target.value)}
          error={errors.description}
          rows={3}
        />
      </form>
    </Modal>
  );
};

export default GoalModal;
