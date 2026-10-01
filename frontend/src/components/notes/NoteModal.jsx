import React, { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import Input from '../ui/Input';
import Textarea from '../ui/Textarea';
import './NoteModal.css';

export const NoteModal = ({
  isOpen,
  onClose,
  onSubmit,
  onDelete,
  initialNote = null,
  availableCategories = [],
  isSubmitting = false,
}) => {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [category, setCategory] = useState('');
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (initialNote) {
      setTitle(initialNote.title || '');
      setContent(initialNote.content || '');
      setCategory(initialNote.category || '');
    } else {
      setTitle('');
      setContent('');
      setCategory('');
    }
    setErrors({});
  }, [initialNote, isOpen]);

  const handleSubmit = (e) => {
    e.preventDefault();
    const newErrors = {};

    if (!title.trim()) {
      newErrors.title = 'Note title is required';
    } else if (title.trim().length > 255) {
      newErrors.title = 'Title must not exceed 255 characters';
    }

    if (category && category.trim().length > 50) {
      newErrors.category = 'Category must not exceed 50 characters';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    const payload = {
      title: title.trim(),
      content: content.trim() || null,
      category: category.trim() || null,
    };

    onSubmit(payload);
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={initialNote ? 'Edit Note' : 'Create New Note'}
      description="Quiet, lightweight knowledge management for your thoughts and insights."
      size="lg"
    >
      <form onSubmit={handleSubmit} className="note-modal__form">
        <Input
          label="Note Title"
          placeholder="e.g. Distributed Systems Architecture takeaways"
          value={title}
          onChange={(e) => {
            setTitle(e.target.value);
            if (errors.title) setErrors({ ...errors, title: null });
          }}
          error={errors.title}
          required
          autoFocus
        />

        <div className="note-modal__category-row">
          <Input
            label="Category (Optional)"
            placeholder="e.g. Engineering, Reading, Architecture"
            value={category}
            onChange={(e) => {
              setCategory(e.target.value);
              if (errors.category) setErrors({ ...errors, category: null });
            }}
            error={errors.category}
          />
          {availableCategories.length > 0 && (
            <div className="note-modal__category-hints">
              <span className="note-modal__hints-label">Suggestions:</span>
              <div className="note-modal__hints-pills">
                {availableCategories.slice(0, 5).map((cat) => (
                  <button
                    key={cat}
                    type="button"
                    className="note-modal__hint-pill"
                    onClick={() => setCategory(cat)}
                  >
                    {cat}
                  </button>
                ))}
              </div>
            </div>
          )}
        </div>

        <Textarea
          label="Content (Plain Text)"
          placeholder="Write your thoughts, observations, summaries, or insights..."
          value={content}
          onChange={(e) => setContent(e.target.value)}
          rows={10}
          className="note-modal__content-textarea"
        />

        <div className="note-modal__actions">
          {initialNote && onDelete && (
            <Button
              type="button"
              variant="danger"
              onClick={() => onDelete(initialNote.id)}
              disabled={isSubmitting}
            >
              Delete Note
            </Button>
          )}
          <div className="note-modal__actions-right">
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
              {initialNote ? 'Save Changes' : 'Create Note'}
            </Button>
          </div>
        </div>
      </form>
    </Modal>
  );
};

export default NoteModal;
