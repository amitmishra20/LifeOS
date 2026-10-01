import React from 'react';
import './NoteCard.css';

export const NoteCard = ({ note, onClick }) => {
  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleDateString(undefined, {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  };

  return (
    <article
      className="note-card"
      onClick={() => onClick(note)}
      tabIndex={0}
      role="button"
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault();
          onClick(note);
        }
      }}
    >
      <div className="note-card__header">
        {note.category ? (
          <span className="note-card__category">{note.category}</span>
        ) : (
          <span className="note-card__category note-card__category--neutral">General</span>
        )}
        <time className="note-card__date">{formatDate(note.updatedAt || note.createdAt)}</time>
      </div>

      <h3 className="note-card__title">{note.title}</h3>

      {note.content ? (
        <p className="note-card__snippet">{note.content}</p>
      ) : (
        <p className="note-card__snippet note-card__snippet--empty">No additional text content.</p>
      )}
    </article>
  );
};

export default NoteCard;
