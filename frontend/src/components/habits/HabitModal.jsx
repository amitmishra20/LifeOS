import React, { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import Input from '../ui/Input';
import Select from '../ui/Select';
import Textarea from '../ui/Textarea';
import goalService from '../../services/goalService';
import './HabitModal.css';

const FREQUENCY_OPTIONS = [
  { value: 'DAILY', label: 'Daily (Every Day)' },
  { value: 'SPECIFIC_DAYS', label: 'Specific Days of the Week' },
  { value: 'WEEKLY_TARGET', label: 'Weekly Target (Flexible)' },
];

const ICON_OPTIONS = [
  { value: 'heart', label: '❤️ Mindfulness & Care' },
  { value: 'workout', label: '🏃 Movement & Fitness' },
  { value: 'book', label: '📖 Reading & Learning' },
  { value: 'code', label: '⚡ Deep Work & Coding' },
  { value: 'water', label: '💧 Health & Hydration' },
  { value: 'sparkle', label: '✦ General Practice' },
];

const WEEKDAYS = [
  { day: 1, label: 'Mon', full: 'Monday' },
  { day: 2, label: 'Tue', full: 'Tuesday' },
  { day: 3, label: 'Wed', full: 'Wednesday' },
  { day: 4, label: 'Thu', full: 'Thursday' },
  { day: 5, label: 'Fri', full: 'Friday' },
  { day: 6, label: 'Sat', full: 'Saturday' },
  { day: 7, label: 'Sun', full: 'Sunday' },
];

export const HabitModal = ({
  isOpen,
  onClose,
  onSubmit,
  habit = null,
  isSubmitting = false,
}) => {
  const isEdit = Boolean(habit && habit.id);

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    frequencyType: 'DAILY',
    targetDaysMask: '1,2,3,4,5',
    targetPerWeek: 4,
    icon: 'sparkle',
    goalId: '',
  });

  const [selectedDays, setSelectedDays] = useState(new Set([1, 2, 3, 4, 5]));
  const [goals, setGoals] = useState([]);
  const [errors, setErrors] = useState({});

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
        console.error('Failed to load goals for habit modal', err);
      }
    };

    loadGoals();
    return () => {
      isMounted = false;
    };
  }, [isOpen]);

  useEffect(() => {
    if (habit) {
      let maskDays = new Set([1, 2, 3, 4, 5]);
      if (habit.targetDaysMask) {
        maskDays = new Set(
          habit.targetDaysMask.split(',').map((d) => parseInt(d.trim(), 10)).filter((n) => !isNaN(n))
        );
      }
      setSelectedDays(maskDays);
      setFormData({
        title: habit.title || '',
        description: habit.description || '',
        frequencyType: habit.frequencyType || 'DAILY',
        targetDaysMask: habit.targetDaysMask || '1,2,3,4,5',
        targetPerWeek: habit.targetPerWeek || 4,
        icon: habit.icon || 'sparkle',
        goalId: habit.goalId ? String(habit.goalId) : '',
      });
    } else {
      setSelectedDays(new Set([1, 2, 3, 4, 5]));
      setFormData({
        title: '',
        description: '',
        frequencyType: 'DAILY',
        targetDaysMask: '1,2,3,4,5',
        targetPerWeek: 4,
        icon: 'sparkle',
        goalId: '',
      });
    }
    setErrors({});
  }, [habit, isOpen]);

  const toggleDay = (dayNum) => {
    const nextDays = new Set(selectedDays);
    if (nextDays.has(dayNum)) {
      if (nextDays.size > 1) {
        nextDays.delete(dayNum);
      }
    } else {
      nextDays.add(dayNum);
    }
    setSelectedDays(nextDays);
    const mask = Array.from(nextDays).sort((a, b) => a - b).join(',');
    setFormData((prev) => ({
      ...prev,
      targetDaysMask: mask,
      targetPerWeek: nextDays.size,
    }));
  };

  const validate = () => {
    const newErrors = {};
    if (!formData.title.trim()) {
      newErrors.title = 'Habit title is required';
    } else if (formData.title.length > 255) {
      newErrors.title = 'Title must be 255 characters or fewer';
    }

    if (formData.frequencyType === 'SPECIFIC_DAYS') {
      if (selectedDays.size === 0) {
        newErrors.targetDays = 'Select at least one day for this habit';
      }
    }

    if (formData.frequencyType === 'WEEKLY_TARGET') {
      const tpw = parseInt(formData.targetPerWeek, 10);
      if (isNaN(tpw) || tpw < 1 || tpw > 7) {
        newErrors.targetPerWeek = 'Weekly target must be between 1 and 7 days';
      }
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
      frequencyType: formData.frequencyType,
      icon: formData.icon || 'sparkle',
      goalId: formData.goalId ? Number(formData.goalId) : null,
    };

    if (formData.frequencyType === 'DAILY') {
      payload.targetDaysMask = null;
      payload.targetPerWeek = 7;
    } else if (formData.frequencyType === 'SPECIFIC_DAYS') {
      const mask = Array.from(selectedDays).sort((a, b) => a - b).join(',');
      payload.targetDaysMask = mask;
      payload.targetPerWeek = selectedDays.size;
    } else if (formData.frequencyType === 'WEEKLY_TARGET') {
      payload.targetDaysMask = null;
      payload.targetPerWeek = parseInt(formData.targetPerWeek, 10);
    }

    onSubmit(payload);
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={isEdit ? 'Edit Habit' : 'Create New Habit'}
      description={isEdit ? 'Adjust your cadence and practice details.' : 'Establish a sustainable daily or weekly rhythm.'}
      size="md"
    >
      <form onSubmit={handleSubmit} className="lifeos-habit-form" noValidate>
        <div className="lifeos-habit-form__row">
          <Input
            id="habit-title"
            label="Habit Title"
            required
            value={formData.title}
            onChange={(e) => setFormData((prev) => ({ ...prev, title: e.target.value }))}
            placeholder="e.g. 20-minute morning run, Daily writing"
            error={errors.title}
            autoFocus
          />
        </div>

        <div className="lifeos-habit-form__row lifeos-habit-form__row--split">
          <Select
            id="habit-icon"
            label="Icon"
            options={ICON_OPTIONS}
            value={formData.icon}
            onChange={(e) => setFormData((prev) => ({ ...prev, icon: e.target.value }))}
          />

          <Select
            id="habit-frequency"
            label="Cadence / Rhythm"
            options={FREQUENCY_OPTIONS}
            value={formData.frequencyType}
            onChange={(e) => setFormData((prev) => ({ ...prev, frequencyType: e.target.value }))}
          />
        </div>

        {formData.frequencyType === 'DAILY' && (
          <div className="lifeos-habit-form__cadence-hint">
            <span className="lifeos-habit-form__cadence-badge">7 days/week</span>
            <span className="lifeos-habit-form__cadence-desc">
              Practiced continuously every calendar day.
            </span>
          </div>
        )}

        {formData.frequencyType === 'SPECIFIC_DAYS' && (
          <div className="lifeos-habit-form__days-group">
            <label className="lifeos-habit-form__label">
              Scheduled Days <span className="lifeos-required">*</span>
            </label>
            <div className="lifeos-habit-form__day-pills" role="group" aria-label="Select days of the week">
              {WEEKDAYS.map(({ day, label, full }) => {
                const isSelected = selectedDays.has(day);
                return (
                  <button
                    key={day}
                    type="button"
                    className={`lifeos-habit-day-pill ${isSelected ? 'lifeos-habit-day-pill--selected' : ''}`}
                    onClick={() => toggleDay(day)}
                    aria-pressed={isSelected}
                    title={full}
                  >
                    {label}
                  </button>
                );
              })}
            </div>
            {errors.targetDays && <span className="lifeos-field-error">{errors.targetDays}</span>}
            <span className="lifeos-habit-form__hint">
              Target: {selectedDays.size} {selectedDays.size === 1 ? 'day' : 'days'} per week
            </span>
          </div>
        )}

        {formData.frequencyType === 'WEEKLY_TARGET' && (
          <div className="lifeos-habit-form__weekly-group">
            <label className="lifeos-habit-form__label" htmlFor="habit-weekly-target">
              Target Days Per Week: <strong>{formData.targetPerWeek}x</strong>
            </label>
            <div className="lifeos-habit-form__target-pills" role="radiogroup">
              {[1, 2, 3, 4, 5, 6, 7].map((num) => (
                <button
                  key={num}
                  type="button"
                  id={`habit-target-${num}`}
                  className={`lifeos-habit-target-pill ${formData.targetPerWeek === num ? 'lifeos-habit-target-pill--selected' : ''}`}
                  onClick={() => setFormData((prev) => ({ ...prev, targetPerWeek: num }))}
                  aria-checked={formData.targetPerWeek === num}
                  role="radio"
                >
                  {num}x
                </button>
              ))}
            </div>
            {errors.targetPerWeek && <span className="lifeos-field-error">{errors.targetPerWeek}</span>}
            <span className="lifeos-habit-form__hint">
              Complete flexibly across any {formData.targetPerWeek} days during each week.
            </span>
          </div>
        )}

        <div className="lifeos-habit-form__row">
          <Select
            id="habit-goal"
            label="Supporting Strategic Goal (Optional)"
            options={[
              { value: '', label: 'None (Standalone Habit)' },
              ...goals.map((g) => ({ value: String(g.id), label: `${g.title} (${g.category})` })),
            ]}
            value={formData.goalId}
            onChange={(e) => setFormData((prev) => ({ ...prev, goalId: e.target.value }))}
            hint="Connects habit lineage to your long-term life vision."
          />
        </div>

        <div className="lifeos-habit-form__row">
          <Textarea
            id="habit-description"
            label="Notes / Intent (Optional)"
            value={formData.description}
            onChange={(e) => setFormData((prev) => ({ ...prev, description: e.target.value }))}
            placeholder="Add context, triggers, or cue routines for this habit..."
            rows={2}
          />
        </div>

        <div className="lifeos-habit-form__actions">
          <Button type="button" variant="secondary" onClick={onClose} disabled={isSubmitting}>
            Cancel
          </Button>
          <Button type="submit" variant="primary" isLoading={isSubmitting} id="habit-submit-btn">
            {isEdit ? 'Save Changes' : 'Create Habit'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default HabitModal;
