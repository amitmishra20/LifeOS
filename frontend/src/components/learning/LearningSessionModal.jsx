import React, { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import Input from '../ui/Input';
import Textarea from '../ui/Textarea';
import './LearningSessionModal.css';

export const LearningSessionModal = ({
  isOpen,
  onClose,
  onSubmit,
  learningItem,
  isSubmitting = false,
}) => {
  const getTodayStr = () => new Date().toISOString().split('T')[0];

  const [formData, setFormData] = useState({
    sessionDate: getTodayStr(),
    durationMinutes: 45,
    topic: '',
    notes: '',
    newProgress: '',
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (!isOpen) return;
    setFormData({
      sessionDate: getTodayStr(),
      durationMinutes: 45,
      topic: '',
      notes: '',
      newProgress: '',
    });
    setErrors({});
  }, [isOpen]);

  const handleChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: null }));
    }
  };

  const validate = () => {
    const errs = {};
    if (!formData.sessionDate) {
      errs.sessionDate = 'Date is required';
    } else if (formData.sessionDate > getTodayStr()) {
      errs.sessionDate = 'Session date cannot be in the future';
    }

    const dur = Number(formData.durationMinutes);
    if (isNaN(dur) || dur <= 0) {
      errs.durationMinutes = 'Duration must be at least 1 minute';
    }

    if (!formData.topic.trim()) {
      errs.topic = 'Topic or chapter title is required';
    } else if (formData.topic.length > 255) {
      errs.topic = 'Topic must not exceed 255 characters';
    }

    if (formData.newProgress !== '' && formData.newProgress !== null && formData.newProgress !== undefined) {
      const prog = Number(formData.newProgress);
      if (isNaN(prog) || prog < 0 || prog > 100) {
        errs.newProgress = 'Progress must be between 0 and 100';
      }
    }

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      sessionDate: formData.sessionDate,
      durationMinutes: Number(formData.durationMinutes),
      topic: formData.topic.trim(),
      notes: formData.notes.trim() || null,
    };

    if (formData.newProgress !== '' && formData.newProgress !== null && formData.newProgress !== undefined) {
      payload.newProgress = Number(formData.newProgress);
    }

    onSubmit(payload);
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="Log Practice / Study Session"
    >
      <form onSubmit={handleSubmit} className="lifeos-session-modal">
        {learningItem && (
          <div className="lifeos-session-modal__item-context">
            <span className="lifeos-session-modal__item-title">
              {learningItem.title}
            </span>
            <span className="lifeos-session-modal__item-stats">
              Invested: {learningItem.totalHoursLearned || 0} hrs • {learningItem.sessionCount || 0} sessions logged
            </span>
          </div>
        )}

        <div className="lifeos-session-modal__row">
          <div className="lifeos-session-modal__field">
            <Input
              id="session-date-input"
              label="Session Date"
              type="date"
              max={getTodayStr()}
              value={formData.sessionDate}
              onChange={(e) => handleChange('sessionDate', e.target.value)}
              error={errors.sessionDate}
              required
            />
          </div>

          <div className="lifeos-session-modal__field">
            <Input
              id="session-duration-input"
              label="Duration (Minutes)"
              type="number"
              min={1}
              step={5}
              placeholder="e.g. 45"
              value={formData.durationMinutes}
              onChange={(e) => handleChange('durationMinutes', e.target.value)}
              error={errors.durationMinutes}
              required
            />
          </div>
        </div>

        <div className="lifeos-session-modal__field">
          <Input
            id="session-topic-input"
            label="Topic / Chapter / Activity"
            placeholder="e.g., Chapter 4: Memory Safety & Lifetimes"
            value={formData.topic}
            onChange={(e) => handleChange('topic', e.target.value)}
            error={errors.topic}
            maxLength={255}
            required
            autoFocus
          />
        </div>

        <div className="lifeos-session-modal__field">
          <Input
            id="session-new-progress-input"
            label={`Update Syllabus Progress (Optional — Current: ${learningItem?.currentProgress || 0}%)`}
            type="number"
            min={0}
            max={100}
            placeholder={`e.g. ${Math.min(100, (learningItem?.currentProgress || 0) + 10)}`}
            value={formData.newProgress}
            onChange={(e) => handleChange('newProgress', e.target.value)}
            error={errors.newProgress}
          />
        </div>

        <div className="lifeos-session-modal__field">
          <Textarea
            id="session-notes-input"
            label="Session Notes & Key Insights (Optional)"
            placeholder="Key takeaways, exercises completed, points of confusion to revisit..."
            value={formData.notes}
            onChange={(e) => handleChange('notes', e.target.value)}
            rows={3}
          />
        </div>

        <div className="lifeos-session-modal__actions">
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
            id="submit-session-btn"
          >
            {isSubmitting ? 'Logging...' : 'Log Session'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default LearningSessionModal;
