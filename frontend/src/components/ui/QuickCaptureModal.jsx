import React, { useState, useEffect, useRef } from 'react';
import Modal from '../ui/Modal';
import Tabs from '../ui/Tabs';
import Input from '../ui/Input';
import Textarea from '../ui/Textarea';
import Button from '../ui/Button';
import Badge from '../ui/Badge';
import { IconTasks, IconNotes, IconCheck, IconTarget } from '../ui/Icons';
import './QuickCaptureModal.css';

const CATEGORIES = ['CAREER', 'HEALTH', 'PERSONAL', 'FINANCE', 'LEARNING'];
const PRIORITIES = [
  { value: 'LOW', label: 'Low', variant: 'default' },
  { value: 'MEDIUM', label: 'Medium', variant: 'warm' },
  { value: 'HIGH', label: 'High', variant: 'amber' },
  { value: 'CRITICAL', label: 'Critical', variant: 'crimson' },
];

export const QuickCaptureModal = ({
  isOpen = false,
  onClose,
  onCapture,
}) => {
  const [captureType, setCaptureType] = useState('task'); // 'task' | 'note'
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('CAREER');
  const [priority, setPriority] = useState('MEDIUM');
  const [feedbackMsg, setFeedbackMsg] = useState(null);

  const inputRef = useRef(null);

  useEffect(() => {
    if (isOpen) {
      setTimeout(() => {
        if (inputRef.current) inputRef.current.focus();
      }, 50);
    } else {
      setTitle('');
      setDescription('');
      setFeedbackMsg(null);
    }
  }, [isOpen]);

  const handleSubmit = (e) => {
    if (e) e.preventDefault();
    if (!title.trim()) return;

    const payload = {
      type: captureType,
      title: title.trim(),
      description: description.trim(),
      category,
      priority,
      createdAt: new Date().toISOString(),
    };

    if (onCapture) {
      onCapture(payload);
    }

    setFeedbackMsg(`${captureType === 'task' ? 'Task' : 'Note'} captured successfully!`);
    setTimeout(() => {
      onClose();
    }, 600);
  };

  const handleKeyDown = (e) => {
    if ((e.metaKey || e.ctrlKey) && e.key === 'Enter') {
      handleSubmit();
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="Quick Capture"
      description="Capture a task or note with immediate execution alignment."
      size="md"
    >
      <form onSubmit={handleSubmit} onKeyDown={handleKeyDown} className="lifeos-quick-capture">
        {/* Type Selector (Task vs Note) */}
        <div className="lifeos-quick-capture__type-row">
          <Tabs
            tabs={[
              { id: 'task', label: 'Task / Action', icon: <IconTasks /> },
              { id: 'note', label: 'Thought / Note', icon: <IconNotes /> },
            ]}
            activeTab={captureType}
            onChange={(tab) => setCaptureType(tab)}
            variant="pills"
          />
        </div>

        {/* Title Input */}
        <div className="lifeos-quick-capture__input-group">
          <Input
            ref={inputRef}
            label={captureType === 'task' ? 'Task Title' : 'Note Title'}
            placeholder={
              captureType === 'task'
                ? 'e.g. Complete distributed lock architecture spec'
                : 'e.g. Reflections on system latency bottlenecks'
            }
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
            autoComplete="off"
          />
        </div>

        {/* Content / Context Textarea */}
        <div className="lifeos-quick-capture__input-group">
          <Textarea
            label="Context / Details (Optional)"
            placeholder="Add supporting context, bullet points, or reference links..."
            rows={2}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </div>

        {/* Category Pill Selection */}
        <div className="lifeos-quick-capture__field">
          <span className="lifeos-quick-capture__field-label">Category</span>
          <div className="lifeos-quick-capture__pill-row">
            {CATEGORIES.map((cat) => (
              <button
                key={cat}
                type="button"
                className={`lifeos-qc-pill ${category === cat ? 'lifeos-qc-pill--active' : ''}`}
                onClick={() => setCategory(cat)}
              >
                {cat}
              </button>
            ))}
          </div>
        </div>

        {/* Priority Selector (for tasks) */}
        {captureType === 'task' && (
          <div className="lifeos-quick-capture__field">
            <span className="lifeos-quick-capture__field-label">Priority</span>
            <div className="lifeos-quick-capture__pill-row">
              {PRIORITIES.map((p) => (
                <button
                  key={p.value}
                  type="button"
                  className={`lifeos-qc-pill ${priority === p.value ? `lifeos-qc-pill--priority-${p.variant}` : ''}`}
                  onClick={() => setPriority(p.value)}
                >
                  <Badge variant={p.variant} size="xs" hasDot={priority === p.value}>
                    {p.label}
                  </Badge>
                </button>
              ))}
            </div>
          </div>
        )}

        {/* Feedback Alert or Submit Action */}
        <div className="lifeos-quick-capture__footer">
          {feedbackMsg ? (
            <div className="lifeos-quick-capture__feedback">
              <IconCheck width="16" height="16" />
              <span>{feedbackMsg}</span>
            </div>
          ) : (
            <div className="lifeos-quick-capture__hint">
              Press <kbd>Ctrl+Enter</kbd> to save immediately
            </div>
          )}

          <div className="lifeos-quick-capture__actions">
            <Button variant="ghost" size="sm" onClick={onClose}>
              Cancel
            </Button>
            <Button
              type="submit"
              variant="primary"
              size="sm"
              disabled={!title.trim()}
              leftIcon={<IconTarget width="14" height="14" />}
            >
              Capture {captureType === 'task' ? 'Task' : 'Note'}
            </Button>
          </div>
        </div>
      </form>
    </Modal>
  );
};

export default QuickCaptureModal;
