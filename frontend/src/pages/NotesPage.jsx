import React, { useState, useEffect, useCallback, useMemo } from 'react';
import noteService from '../services/noteService';
import NoteCard from '../components/notes/NoteCard';
import NoteModal from '../components/notes/NoteModal';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import EmptyState from '../components/ui/EmptyState';
import LoadingState from '../components/ui/LoadingState';
import { IconNotes, IconPlus, IconSearch } from '../components/ui/Icons';
import './NotesPage.css';

export const NotesPage = () => {
  // State
  const [notes, setNotes] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [activeSearch, setActiveSearch] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingNote, setEditingNote] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Fetch notes from server with optional search and category
  const loadNotes = useCallback(async (query = activeSearch, category = selectedCategory) => {
    try {
      setIsLoading(true);
      setError(null);
      const params = {};
      if (query && query.trim()) params.search = query.trim();
      if (category && category !== 'ALL') params.category = category.trim();

      const notesData = await noteService.getNotes(params);
      const fetchedNotes = notesData || [];
      setNotes(fetchedNotes);

      // Keep tracked categories updated from returned notes
      setCategories((prev) => {
        const set = new Set(prev);
        fetchedNotes.forEach((n) => {
          if (n.category && n.category.trim()) {
            set.add(n.category.trim());
          }
        });
        return Array.from(set).sort();
      });
    } catch (err) {
      console.error('Failed to load notes', err);
      setError('Unable to load notes. Please refresh or try again.');
    } finally {
      setIsLoading(false);
    }
  }, [activeSearch, selectedCategory]);

  useEffect(() => {
    loadNotes(activeSearch, selectedCategory);
  }, [loadNotes, activeSearch, selectedCategory]);

  // Debounced search trigger
  useEffect(() => {
    const timer = setTimeout(() => {
      setActiveSearch(searchQuery);
    }, 300);
    return () => clearTimeout(timer);
  }, [searchQuery]);

  // Actions
  const handleCreateNoteClick = () => {
    setEditingNote(null);
    setIsModalOpen(true);
  };

  const handleNoteCardClick = (note) => {
    setEditingNote(note);
    setIsModalOpen(true);
  };

  const handleModalSubmit = async (payload) => {
    try {
      setIsSubmitting(true);
      if (editingNote) {
        await noteService.updateNote(editingNote.id, payload);
      } else {
        await noteService.createNote(payload);
      }
      setIsModalOpen(false);
      setEditingNote(null);
      await loadNotes(activeSearch, selectedCategory);
    } catch (err) {
      console.error('Failed to save note', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDeleteNote = async (id) => {
    if (!window.confirm('Are you sure you want to delete this note?')) return;
    try {
      setIsSubmitting(true);
      await noteService.deleteNote(id);
      setIsModalOpen(false);
      setEditingNote(null);
      await loadNotes(activeSearch, selectedCategory);
    } catch (err) {
      console.error('Failed to delete note', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClearFilters = () => {
    setSearchQuery('');
    setActiveSearch('');
    setSelectedCategory('ALL');
  };

  const isFiltering = activeSearch.trim() !== '' || selectedCategory !== 'ALL';

  return (
    <div className="notes-page">
      {/* 1. Header */}
      <header className="notes-page__header">
        <div className="notes-page__header-left">
          <span className="notes-page__kicker">KNOWLEDGE & CAPTURE</span>
          <h1 className="notes-page__title">Notes & Insights</h1>
          <p className="notes-page__subtitle">
            Quiet, distraction-free knowledge capture and organization for your learning and ideas.
          </p>
        </div>

        <div className="notes-page__header-actions">
          <Button
            variant="primary"
            icon={<IconPlus />}
            onClick={handleCreateNoteClick}
            id="create-note-btn"
          >
            Create Note
          </Button>
        </div>
      </header>

      {/* 2. Controls: Search & Category Filter Pills */}
      <section className="notes-controls">
        <div className="notes-controls__search-wrapper">
          <Input
            placeholder="Search notes by title or content..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            leftIcon={<IconSearch />}
            className="notes-search-input"
            aria-label="Search notes"
          />
          {searchQuery && (
            <button
              type="button"
              className="notes-search-clear-btn"
              onClick={() => setSearchQuery('')}
              aria-label="Clear search input"
            >
              &times;
            </button>
          )}
        </div>

        <div className="notes-controls__categories" role="tablist" aria-label="Filter notes by category">
          <button
            type="button"
            className={`notes-category-pill ${selectedCategory === 'ALL' ? 'active' : ''}`}
            onClick={() => setSelectedCategory('ALL')}
          >
            All Notes
          </button>
          {categories.map((cat) => (
            <button
              key={cat}
              type="button"
              className={`notes-category-pill ${selectedCategory === cat ? 'active' : ''}`}
              onClick={() => setSelectedCategory(cat)}
            >
              {cat}
            </button>
          ))}
        </div>
      </section>

      {/* 3. Content Area */}
      {isLoading ? (
        <LoadingState message="Loading your notes..." />
      ) : error ? (
        <div className="notes-page__error">
          <p>{error}</p>
          <Button variant="secondary" onClick={() => loadNotes(activeSearch, selectedCategory)}>
            Retry
          </Button>
        </div>
      ) : notes.length === 0 ? (
        <div className="notes-page__empty">
          {isFiltering ? (
            <EmptyState
              icon={<IconSearch />}
              title="No Matching Notes"
              description="No notes matched your search query or category filter. Try refining your keywords or clearing the filter."
              actionLabel="Clear Filters"
              onAction={handleClearFilters}
            />
          ) : (
            <EmptyState
              icon={<IconNotes />}
              title="No Notes Captured Yet"
              description="Capture key insights, system architecture notes, learning takeaways, and ideas in a quiet, distraction-free space."
              actionLabel="Create Note"
              onAction={handleCreateNoteClick}
            />
          )}
        </div>
      ) : (
        <main className="notes-grid" aria-label="Notes collection">
          {notes.map((note) => (
            <NoteCard
              key={note.id}
              note={note}
              onClick={handleNoteCardClick}
            />
          ))}
        </main>
      )}

      {/* 4. Create / Edit Note Modal */}
      <NoteModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setEditingNote(null);
        }}
        onSubmit={handleModalSubmit}
        onDelete={editingNote ? handleDeleteNote : null}
        initialNote={editingNote}
        availableCategories={categories}
        isSubmitting={isSubmitting}
      />
    </div>
  );
};

export default NotesPage;
