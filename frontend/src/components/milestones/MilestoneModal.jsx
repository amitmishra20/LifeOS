import React, { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import Input from '../ui/Input';
import Select from '../ui/Select';
import Textarea from '../ui/Textarea';
import './MilestoneModal.css';

const STATUS_OPTIONS = [
  { value: 'PENDING', label: 'Pending' },
  { value: 'ACTIVE', label: 'Active' },
  { value: 'COMPLETED', label: 'Completed' },
];

export const MilestoneModal = ({
  isOpen,
  onClose,
  onSubmit,
  milestone = null,
  isSubmitting = false,
}) => {
  const isEdit = Boolean(milestone && milestone.id);

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    targetDate: '',
    status: 'PENDING',
    orderIndex: '',
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (milestone) {
      setFormData({
        title: milestone.title || '',
        description: milestone.description || '',
        targetDate: milestone.targetDate || '',
        status: milestone.status || 'PENDING',
        orderIndex: milestone.orderIndex != null ? String(milestone.orderIndex) : '',
      });
    } else {
      setFormData({
        title: '',
        description: '',
        targetDate: '',
        status: 'PENDING',
        orderIndex: '',
      });
    }
    setErrors({});
  }, [milestone, isOpen]);

  const handleChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: null }));
    }
  };

  const validate = () => {
    const errs = {};
    if (!formData.title.trim()) {
      errs.title = 'Milestone title is required';
    } else if (formData.title.length > 255) {
      errs.title = 'Title cannot exceed 255 characters';
    }

    if (formData.description && formData.description.length > 2000) {
      errs.description = 'Description cannot exceed 2000 characters';
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
      targetDate: formData.targetDate || null,
      status: formData.status,
    };

    if (formData.orderIndex !== '') {
      payload.orderIndex = parseInt(formData.orderIndex, 10);
    }

    onSubmit(payload);
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={isEdit ? 'Edit Milestone' : 'Establish Milestone'}
      description={
        isEdit
          ? 'Modify phase timing, status, or context.'
          : 'Define a distinct waypoint or phase along the path to your goal.'
      }
      footer={
        <div className="lifeos-modal-footer-actions">
          <Button variant="ghost" size="md" onClick={onClose} disabled={isSubmitting}>
            Cancel
          </Button>
          <Button variant="primary" size="md" onClick={handleSubmit} loading={isSubmitting}>
            {isEdit ? 'Save Changes' : 'Create Milestone'}
          </Button>
        </div>
      }
    >
      <form onSubmit={handleSubmit} className="lifeos-milestone-form">
        <Input
          label="Milestone Title"
          placeholder="e.g., Phase 1: Core Systems Mastery"
          value={formData.title}
          onChange={(e) => handleChange('title', e.target.value)}
          error={errors.title}
          autoFocus
          required
        />

        <div className="lifeos-milestone-form__grid">
          <Select
            label="Phase Status"
            options={STATUS_OPTIONS}
            value={formData.status}
            onChange={(e) => handleChange('status', e.target.value)}
          />

          <Input
            type="date"
            label="Target Date"
            value={formData.targetDate}
            onChange={(e) => handleChange('targetDate', e.target.value)}
          />
        </div>

        <Textarea
          label="Phase Scope & Criteria"
          placeholder="What defines completion of this milestone?"
          value={formData.description}
          onChange={(e) => handleChange('description', e.target.value)}
          error={errors.description}
          rows={3}
        />
      </form>
    </Modal>
  );
};

export default MilestoneModal;
