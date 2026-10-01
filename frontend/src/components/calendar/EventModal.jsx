import React, { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import Input from '../ui/Input';
import Textarea from '../ui/Textarea';
import './EventModal.css';

export const EventModal = ({
  isOpen,
  onClose,
  onSubmit,
  onDelete,
  initialEvent = null,
  initialDate = null,
  isSubmitting = false,
}) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (initialEvent) {
      setTitle(initialEvent.title || '');
      setDescription(initialEvent.description || '');
      setStartTime(formatDateTimeForInput(initialEvent.startTime));
      setEndTime(formatDateTimeForInput(initialEvent.endTime));
    } else {
      const baseDate = initialDate ? new Date(initialDate) : new Date();
      baseDate.setHours(10, 0, 0, 0);
      const endDefault = new Date(baseDate.getTime() + 60 * 60 * 1000);

      setTitle('');
      setDescription('');
      setStartTime(formatDateTimeForInput(baseDate));
      setEndTime(formatDateTimeForInput(endDefault));
    }
    setErrors({});
  }, [initialEvent, initialDate, isOpen]);

  const formatDateTimeForInput = (dateTime) => {
    if (!dateTime) return '';
    const d = new Date(dateTime);
    if (isNaN(d.getTime())) return '';
    const pad = (num) => String(num).padStart(2, '0');
    const year = d.getFullYear();
    const month = pad(d.getMonth() + 1);
    const day = pad(d.getDate());
    const hours = pad(d.getHours());
    const minutes = pad(d.getMinutes());
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const newErrors = {};

    if (!title.trim()) {
      newErrors.title = 'Event title is required';
    }

    if (!startTime) {
      newErrors.startTime = 'Start time is required';
    }

    if (startTime && endTime && new Date(endTime) < new Date(startTime)) {
      newErrors.endTime = 'End time cannot be before start time';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    const payload = {
      title: title.trim(),
      description: description.trim() || null,
      startTime: startTime,
      endTime: endTime || null,
      eventType: 'CUSTOM_EVENT',
    };

    onSubmit(payload);
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={initialEvent ? 'Edit Scheduled Event' : 'Schedule Custom Event'}
      description="Create a time-oriented commitment in your LifeOS schedule"
      size="md"
    >
      <form onSubmit={handleSubmit} className="event-modal__form">
        <Input
          label="Event Title"
          placeholder="e.g. Weekly Strategy Sync"
          value={title}
          onChange={(e) => {
            setTitle(e.target.value);
            if (errors.title) setErrors({ ...errors, title: null });
          }}
          error={errors.title}
          required
          autoFocus
        />

        <div className="event-modal__row">
          <Input
            label="Start Time"
            type="datetime-local"
            value={startTime}
            onChange={(e) => {
              setStartTime(e.target.value);
              if (errors.startTime) setErrors({ ...errors, startTime: null });
            }}
            error={errors.startTime}
            required
          />

          <Input
            label="End Time (Optional)"
            type="datetime-local"
            value={endTime}
            onChange={(e) => {
              setEndTime(e.target.value);
              if (errors.endTime) setErrors({ ...errors, endTime: null });
            }}
            error={errors.endTime}
          />
        </div>

        <Textarea
          label="Description / Context (Optional)"
          placeholder="Add agenda, location, meeting link, or context..."
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          rows={3}
        />

        <div className="event-modal__actions">
          {initialEvent && onDelete && (
            <Button
              type="button"
              variant="danger"
              onClick={() => onDelete(initialEvent.id)}
              disabled={isSubmitting}
            >
              Delete Event
            </Button>
          )}
          <div className="event-modal__actions-right">
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
              {initialEvent ? 'Save Changes' : 'Schedule Event'}
            </Button>
          </div>
        </div>
      </form>
    </Modal>
  );
};

export default EventModal;
