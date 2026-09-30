import React, { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import Input from '../ui/Input';
import Select from '../ui/Select';
import Textarea from '../ui/Textarea';
import goalService from '../../services/goalService';
import './LearningModal.css';

const CATEGORY_OPTIONS = [
  { value: 'TECHNICAL', label: '💻 Technical & Engineering' },
  { value: 'LANGUAGE', label: '🌐 Language & Linguistics' },
  { value: 'ACADEMIC', label: '📚 Academic & Sciences' },
  { value: 'PROFESSIONAL', label: '💼 Professional & Leadership' },
  { value: 'CREATIVE', label: '🎨 Creative & Arts' },
  { value: 'PERSONAL', label: '🌱 Personal Growth & Philosophy' },
];

export const LearningModal = ({
  isOpen,
  onClose,
  onSubmit,
  learningItem = null,
  isSubmitting = false,
}) => {
  const isEdit = Boolean(learningItem && learningItem.id);

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: 'TECHNICAL',
    targetProgress: 100,
    currentProgress: 0,
    goalId: '',
  });

  const [goals, setGoals] = useState([]);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (!isOpen) return;

    // Load active goals for contextual linking
    const loadGoals = async () => {
      try {
        const goalsData = await goalService.getGoals();
        setGoals(Array.isArray(goalsData) ? goalsData : []);
      } catch (err) {
        console.warn('Failed to load goals for learning item context:', err);
      }
    };
    loadGoals();

    if (learningItem) {
      setFormData({
        title: learningItem.title || '',
        description: learningItem.description || '',
        category: learningItem.category || 'TECHNICAL',
        targetProgress: learningItem.targetProgress || 100,
        currentProgress: learningItem.currentProgress || 0,
        goalId: learningItem.goalId ? String(learningItem.goalId) : '',
      });
    } else {
      setFormData({
        title: '',
        description: '',
        category: 'TECHNICAL',
        targetProgress: 100,
        currentProgress: 0,
        goalId: '',
      });
    }
    setErrors({});
  }, [isOpen, learningItem]);

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
    } else if (formData.title.length > 120) {
      errs.title = 'Title must not exceed 120 characters';
    }

    if (formData.description && formData.description.length > 500) {
      errs.description = 'Description must not exceed 500 characters';
    }

    const target = Number(formData.targetProgress);
    if (isNaN(target) || target < 1 || target > 100) {
      errs.targetProgress = 'Target progress must be between 1 and 100';
    }

    if (!isEdit) {
      const current = Number(formData.currentProgress);
      if (isNaN(current) || current < 0 || current > 100) {
        errs.currentProgress = 'Initial progress must be between 0 and 100';
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
      targetProgress: Number(formData.targetProgress),
      goalId: formData.goalId ? Number(formData.goalId) : null,
    };

    if (!isEdit) {
      payload.currentProgress = Number(formData.currentProgress) || 0;
    }

    onSubmit(payload);
  };

  const goalOptions = [
    { value: '', label: 'None (Self-directed Learning)' },
    ...goals.map((g) => ({
      value: String(g.id),
      label: `🎯 ${g.title}`,
    })),
  ];

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={isEdit ? 'Edit Learning Subject' : 'New Learning Subject'}
    >
      <form onSubmit={handleSubmit} className="lifeos-learning-modal">
        <div className="lifeos-learning-modal__field">
          <Input
            id="learning-title-input"
            label="Subject Title"
            placeholder="e.g., Distributed Systems, Rust, German B1"
            value={formData.title}
            onChange={(e) => handleChange('title', e.target.value)}
            error={errors.title}
            maxLength={120}
            required
            autoFocus
          />
        </div>

        <div className="lifeos-learning-modal__row">
          <div className="lifeos-learning-modal__field">
            <Select
              id="learning-category-select"
              label="Domain Category"
              value={formData.category}
              onChange={(val) => handleChange('category', val)}
              options={CATEGORY_OPTIONS}
            />
          </div>

          <div className="lifeos-learning-modal__field">
            <Select
              id="learning-goal-select"
              label="Connected Goal (Optional)"
              value={formData.goalId}
              onChange={(val) => handleChange('goalId', val)}
              options={goalOptions}
            />
          </div>
        </div>

        <div className="lifeos-learning-modal__row">
          <div className="lifeos-learning-modal__field">
            <Input
              id="learning-target-progress-input"
              label="Target Progress (%)"
              type="number"
              min={1}
              max={100}
              value={formData.targetProgress}
              onChange={(e) => handleChange('targetProgress', e.target.value)}
              error={errors.targetProgress}
              required
            />
          </div>

          {!isEdit && (
            <div className="lifeos-learning-modal__field">
              <Input
                id="learning-current-progress-input"
                label="Current Progress (%)"
                type="number"
                min={0}
                max={100}
                value={formData.currentProgress}
                onChange={(e) => handleChange('currentProgress', e.target.value)}
                error={errors.currentProgress}
              />
            </div>
          )}
        </div>

        <div className="lifeos-learning-modal__field">
          <Textarea
            id="learning-description-input"
            label="Why It Matters / Syllabus / Objective"
            placeholder="Outline your curriculum, core concepts to master, or why this capability is valuable."
            value={formData.description}
            onChange={(e) => handleChange('description', e.target.value)}
            error={errors.description}
            rows={3}
            maxLength={500}
          />
        </div>

        <div className="lifeos-learning-modal__actions">
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
            disabled={isSubmitting}
            id="save-learning-btn"
          >
            {isSubmitting ? 'Saving...' : isEdit ? 'Save Changes' : 'Create Subject'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default LearningModal;
