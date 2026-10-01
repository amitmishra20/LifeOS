import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import calendarService from '../services/calendarService';
import EventModal from '../components/calendar/EventModal';
import Button from '../components/ui/Button';
import EmptyState from '../components/ui/EmptyState';
import LoadingState from '../components/ui/LoadingState';
import { IconCalendar, IconPlus } from '../components/ui/Icons';
import './CalendarPage.css';

const DAYS_OF_WEEK = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
const MONTH_NAMES = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'
];

export const CalendarPage = () => {
  const navigate = useNavigate();

  // Calendar navigation state
  const [currentDate, setCurrentDate] = useState(new Date());
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [viewMode, setViewMode] = useState('month'); // 'month' | 'agenda'
  const [filterType, setFilterType] = useState('ALL'); // 'ALL' | 'CUSTOM_EVENT' | 'TASK_DEADLINE' | 'GOAL_DEADLINE' | 'LEARNING_SESSION'

  // Data state
  const [feedItems, setFeedItems] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  // Modal state
  const [isEventModalOpen, setIsEventModalOpen] = useState(false);
  const [editingEvent, setEditingEvent] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Compute month range for unified feed fetch
  const { startDateStr, endDateStr } = useMemo(() => {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();

    // Start from previous month with buffer
    const start = new Date(year, month - 1, 20);
    // End at next month with buffer
    const end = new Date(year, month + 2, 10);

    const pad = (n) => String(n).padStart(2, '0');
    const toDateStr = (d) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;

    return {
      startDateStr: toDateStr(start),
      endDateStr: toDateStr(end),
    };
  }, [currentDate]);

  // Fetch unified calendar feed via canonical GET /api/v1/calendar?start=...&end=...
  const loadCalendarData = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await calendarService.getUnifiedFeed(startDateStr, endDateStr);
      setFeedItems(data || []);
    } catch (err) {
      console.error('Failed to load calendar data', err);
      setError('Unable to load calendar commitments. Please refresh.');
    } finally {
      setIsLoading(false);
    }
  }, [startDateStr, endDateStr]);

  useEffect(() => {
    loadCalendarData();
  }, [loadCalendarData]);

  // Filtered items
  const filteredItems = useMemo(() => {
    if (filterType === 'ALL') return feedItems;
    return feedItems.filter((item) => item.itemType === filterType);
  }, [feedItems, filterType]);

  // Group items by date string YYYY-MM-DD
  const itemsByDate = useMemo(() => {
    const map = {};
    filteredItems.forEach((item) => {
      if (!item.startTime) return;
      const dateKey = item.startTime.substring(0, 10);
      if (!map[dateKey]) map[dateKey] = [];
      map[dateKey].push(item);
    });
    return map;
  }, [filteredItems]);

  // Calendar grid calculations
  const calendarGrid = useMemo(() => {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();

    const firstDayIndex = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const daysInPrevMonth = new Date(year, month, 0).getDate();

    const grid = [];
    const pad = (n) => String(n).padStart(2, '0');

    // Previous month filler days
    for (let i = firstDayIndex - 1; i >= 0; i--) {
      const day = daysInPrevMonth - i;
      const prevDate = new Date(year, month - 1, day);
      const dateStr = `${prevDate.getFullYear()}-${pad(prevDate.getMonth() + 1)}-${pad(day)}`;
      grid.push({
        date: prevDate,
        dateStr,
        dayNumber: day,
        isCurrentMonth: false,
      });
    }

    // Current month days
    for (let day = 1; day <= daysInMonth; day++) {
      const curDate = new Date(year, month, day);
      const dateStr = `${year}-${pad(month + 1)}-${pad(day)}`;
      grid.push({
        date: curDate,
        dateStr,
        dayNumber: day,
        isCurrentMonth: true,
      });
    }

    // Next month filler days to complete grid to 35 or 42 cells
    const remaining = (7 - (grid.length % 7)) % 7;
    for (let day = 1; day <= remaining; day++) {
      const nextDate = new Date(year, month + 1, day);
      const dateStr = `${nextDate.getFullYear()}-${pad(nextDate.getMonth() + 1)}-${pad(day)}`;
      grid.push({
        date: nextDate,
        dateStr,
        dayNumber: day,
        isCurrentMonth: false,
      });
    }

    return grid;
  }, [currentDate]);

  // Selected date key
  const selectedDateStr = useMemo(() => {
    const pad = (n) => String(n).padStart(2, '0');
    return `${selectedDate.getFullYear()}-${pad(selectedDate.getMonth() + 1)}-${pad(selectedDate.getDate())}`;
  }, [selectedDate]);

  const selectedDayItems = useMemo(() => {
    return itemsByDate[selectedDateStr] || [];
  }, [itemsByDate, selectedDateStr]);

  const todayStr = useMemo(() => {
    const now = new Date();
    const pad = (n) => String(n).padStart(2, '0');
    return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`;
  }, []);

  // Navigation actions
  const handlePrevMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 1));
  };

  const handleNextMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 1));
  };

  const handleToday = () => {
    const now = new Date();
    setCurrentDate(now);
    setSelectedDate(now);
  };

  // Event handlers
  const handleCreateEventClick = (date = null) => {
    setEditingEvent(null);
    if (date) {
      setSelectedDate(date);
    }
    setIsEventModalOpen(true);
  };

  const handleItemClick = async (item) => {
    if (item.itemType === 'CUSTOM_EVENT') {
      try {
        const event = await calendarService.getEvent(item.sourceId);
        setEditingEvent(event);
        setIsEventModalOpen(true);
      } catch (err) {
        console.error('Failed to load event for editing', err);
      }
    } else if (item.linkUrl) {
      navigate(item.linkUrl);
    }
  };

  const handleModalSubmit = async (payload) => {
    try {
      setIsSubmitting(true);
      if (editingEvent) {
        await calendarService.updateEvent(editingEvent.id, payload);
      } else {
        await calendarService.createEvent(payload);
      }
      setIsEventModalOpen(false);
      setEditingEvent(null);
      await loadCalendarData();
    } catch (err) {
      console.error('Failed to save event', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDeleteEvent = async (id) => {
    if (!window.confirm('Are you sure you want to delete this event?')) return;
    try {
      setIsSubmitting(true);
      await calendarService.deleteEvent(id);
      setIsEventModalOpen(false);
      setEditingEvent(null);
      await loadCalendarData();
    } catch (err) {
      console.error('Failed to delete event', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const getItemBadgeClass = (itemType) => {
    switch (itemType) {
      case 'TASK_DEADLINE':
        return 'cal-badge--task';
      case 'GOAL_DEADLINE':
        return 'cal-badge--goal';
      case 'LEARNING_SESSION':
        return 'cal-badge--learning';
      case 'CUSTOM_EVENT':
      default:
        return 'cal-badge--custom';
    }
  };

  const formatItemTypeLabel = (itemType) => {
    switch (itemType) {
      case 'TASK_DEADLINE':
        return 'TASK DEADLINE';
      case 'GOAL_DEADLINE':
        return 'GOAL DEADLINE';
      case 'LEARNING_SESSION':
        return 'LEARNING SESSION';
      case 'CUSTOM_EVENT':
      default:
        return 'CUSTOM EVENT';
    }
  };

  const formatTimeRange = (startTime, endTime) => {
    if (!startTime) return '';
    const s = new Date(startTime);
    const startStr = s.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    if (!endTime) return startStr;
    const e = new Date(endTime);
    const endStr = e.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    return `${startStr} – ${endStr}`;
  };

  return (
    <div className="calendar-page">
      {/* 1. Header & Navigation */}
      <header className="calendar-page__header">
        <div className="calendar-page__header-left">
          <span className="calendar-page__kicker">PLAN & SCHEDULE</span>
          <h1 className="calendar-page__title">Calendar & Schedule</h1>
          <p className="calendar-page__subtitle">
            Centralize your custom events, task deadlines, goal targets, and learning sessions.
          </p>
        </div>

        <div className="calendar-page__header-actions">
          <Button
            variant="primary"
            icon={<IconPlus />}
            onClick={() => handleCreateEventClick(selectedDate)}
          >
            Schedule Event
          </Button>
        </div>
      </header>

      {/* 2. Controls Strip */}
      <section className="calendar-controls">
        <div className="calendar-controls__nav">
          <Button variant="ghost" size="sm" onClick={handlePrevMonth} aria-label="Previous month">
            &larr;
          </Button>
          <h2 className="calendar-controls__month-label">
            {MONTH_NAMES[currentDate.getMonth()]} {currentDate.getFullYear()}
          </h2>
          <Button variant="ghost" size="sm" onClick={handleNextMonth} aria-label="Next month">
            &rarr;
          </Button>
          <Button variant="secondary" size="sm" onClick={handleToday}>
            Today
          </Button>
        </div>

        {/* Filter Pills */}
        <div className="calendar-controls__filters">
          <button
            type="button"
            className={`calendar-filter-btn ${filterType === 'ALL' ? 'active' : ''}`}
            onClick={() => setFilterType('ALL')}
          >
            All Commitments
          </button>
          <button
            type="button"
            className={`calendar-filter-btn ${filterType === 'CUSTOM_EVENT' ? 'active' : ''}`}
            onClick={() => setFilterType('CUSTOM_EVENT')}
          >
            <span className="cal-dot cal-dot--custom" /> Custom Events
          </button>
          <button
            type="button"
            className={`calendar-filter-btn ${filterType === 'TASK_DEADLINE' ? 'active' : ''}`}
            onClick={() => setFilterType('TASK_DEADLINE')}
          >
            <span className="cal-dot cal-dot--task" /> Task Deadlines
          </button>
          <button
            type="button"
            className={`calendar-filter-btn ${filterType === 'GOAL_DEADLINE' ? 'active' : ''}`}
            onClick={() => setFilterType('GOAL_DEADLINE')}
          >
            <span className="cal-dot cal-dot--goal" /> Goal Deadlines
          </button>
          <button
            type="button"
            className={`calendar-filter-btn ${filterType === 'LEARNING_SESSION' ? 'active' : ''}`}
            onClick={() => setFilterType('LEARNING_SESSION')}
          >
            <span className="cal-dot cal-dot--learning" /> Learning Sessions
          </button>
        </div>

        {/* View Mode Toggle */}
        <div className="calendar-controls__views">
          <button
            type="button"
            className={`calendar-view-btn ${viewMode === 'month' ? 'active' : ''}`}
            onClick={() => setViewMode('month')}
          >
            Month
          </button>
          <button
            type="button"
            className={`calendar-view-btn ${viewMode === 'agenda' ? 'active' : ''}`}
            onClick={() => setViewMode('agenda')}
          >
            Agenda
          </button>
        </div>
      </section>

      {/* 3. Main Workspace */}
      {isLoading ? (
        <LoadingState message="Loading your schedule & commitments..." />
      ) : error ? (
        <div className="calendar-page__error">
          <p>{error}</p>
          <Button variant="secondary" onClick={loadCalendarData}>Retry</Button>
        </div>
      ) : (
        <div className="calendar-workspace">
          {/* Month Grid View */}
          {viewMode === 'month' && (
            <div className="calendar-grid-card">
              <div className="calendar-grid__days-header">
                {DAYS_OF_WEEK.map((day) => (
                  <div key={day} className="calendar-grid__day-col">
                    {day}
                  </div>
                ))}
              </div>

              <div className="calendar-grid__body">
                {calendarGrid.map((cell) => {
                  const isSelected = cell.dateStr === selectedDateStr;
                  const isToday = cell.dateStr === todayStr;
                  const dayItems = itemsByDate[cell.dateStr] || [];

                  return (
                    <div
                      key={cell.dateStr}
                      className={`calendar-cell ${!cell.isCurrentMonth ? 'calendar-cell--muted' : ''} ${
                        isSelected ? 'calendar-cell--selected' : ''
                      } ${isToday ? 'calendar-cell--today' : ''}`}
                      onClick={() => setSelectedDate(cell.date)}
                      onDoubleClick={() => handleCreateEventClick(cell.date)}
                    >
                      <div className="calendar-cell__header">
                        <span className={`calendar-cell__day-num ${isToday ? 'calendar-cell__day-num--today' : ''}`}>
                          {cell.dayNumber}
                        </span>
                        {dayItems.length > 0 && (
                          <span className="calendar-cell__count">{dayItems.length}</span>
                        )}
                      </div>

                      <div className="calendar-cell__items">
                        {dayItems.slice(0, 3).map((item) => (
                          <div
                            key={item.id}
                            className={`cal-chip ${getItemBadgeClass(item.itemType)}`}
                            onClick={(e) => {
                              e.stopPropagation();
                              handleItemClick(item);
                            }}
                            title={item.title}
                          >
                            <span className="cal-chip__title">{item.title}</span>
                          </div>
                        ))}
                        {dayItems.length > 3 && (
                          <div className="cal-chip--more">+{dayItems.length - 3} more</div>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* Agenda View */}
          {viewMode === 'agenda' && (
            <div className="calendar-agenda-card">
              {filteredItems.length === 0 ? (
                <EmptyState
                  icon={<IconCalendar />}
                  title="No Scheduled Commitments"
                  description="Your schedule is clear for this period. Add a custom event or schedule tasks and goals."
                  actionLabel="Schedule Event"
                  onAction={() => handleCreateEventClick(new Date())}
                />
              ) : (
                <div className="calendar-agenda-list">
                  {Object.keys(itemsByDate).sort().map((dateStr) => {
                    const items = itemsByDate[dateStr];
                    const dateObj = new Date(dateStr + 'T00:00:00');
                    const isToday = dateStr === todayStr;

                    return (
                      <div key={dateStr} className={`agenda-day-group ${isToday ? 'agenda-day-group--today' : ''}`}>
                        <div className="agenda-day-header">
                          <span className="agenda-day-title">
                            {dateObj.toLocaleDateString(undefined, { weekday: 'short', month: 'short', day: 'numeric', year: 'numeric' })}
                          </span>
                          {isToday && <span className="agenda-today-pill">TODAY</span>}
                        </div>

                        <div className="agenda-items-list">
                          {items.map((item) => (
                            <div
                              key={item.id}
                              className={`agenda-item ${getItemBadgeClass(item.itemType)}`}
                              onClick={() => handleItemClick(item)}
                            >
                              <div className="agenda-item__time">
                                {formatTimeRange(item.startTime, item.endTime)}
                              </div>
                              <div className="agenda-item__details">
                                <h4 className="agenda-item__title">{item.title}</h4>
                                {item.description && (
                                  <p className="agenda-item__desc">{item.description}</p>
                                )}
                              </div>
                              <div className="agenda-item__tag">
                                {formatItemTypeLabel(item.itemType)}
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          )}

          {/* Selected Day Agenda Drawer (Side Panel on Desktop) */}
          <aside className="calendar-day-drawer">
            <div className="calendar-day-drawer__header">
              <div>
                <span className="calendar-day-drawer__kicker">SELECTED DAY</span>
                <h3 className="calendar-day-drawer__title">
                  {selectedDate.toLocaleDateString(undefined, {
                    weekday: 'long',
                    month: 'short',
                    day: 'numeric',
                  })}
                </h3>
              </div>
              <Button
                variant="ghost"
                size="sm"
                icon={<IconPlus />}
                onClick={() => handleCreateEventClick(selectedDate)}
              >
                Add
              </Button>
            </div>

            <div className="calendar-day-drawer__content">
              {selectedDayItems.length === 0 ? (
                <div className="calendar-day-drawer__empty">
                  <p>No commitments scheduled for this day.</p>
                  <Button
                    variant="secondary"
                    size="sm"
                    onClick={() => handleCreateEventClick(selectedDate)}
                  >
                    Schedule Event
                  </Button>
                </div>
              ) : (
                <div className="calendar-day-drawer__list">
                  {selectedDayItems.map((item) => (
                    <div
                      key={item.id}
                      className={`day-commitment-card ${getItemBadgeClass(item.itemType)}`}
                      onClick={() => handleItemClick(item)}
                    >
                      <div className="day-commitment-card__header">
                        <span className="day-commitment-card__time">
                          {formatTimeRange(item.startTime, item.endTime)}
                        </span>
                        <span className="day-commitment-card__type">
                          {formatItemTypeLabel(item.itemType)}
                        </span>
                      </div>
                      <h4 className="day-commitment-card__title">{item.title}</h4>
                      {item.description && (
                        <p className="day-commitment-card__desc">{item.description}</p>
                      )}
                      {item.linkUrl && item.itemType !== 'CUSTOM_EVENT' && (
                        <span className="day-commitment-card__link">
                          View details &rarr;
                        </span>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </aside>
        </div>
      )}

      {/* 4. Event Create / Edit Modal */}
      <EventModal
        isOpen={isEventModalOpen}
        onClose={() => {
          setIsEventModalOpen(false);
          setEditingEvent(null);
        }}
        onSubmit={handleModalSubmit}
        onDelete={editingEvent ? handleDeleteEvent : null}
        initialEvent={editingEvent}
        initialDate={selectedDate}
        isSubmitting={isSubmitting}
      />
    </div>
  );
};

export default CalendarPage;
