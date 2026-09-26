import React, { useState, useEffect, useCallback, useMemo } from 'react';
import taskService from '../services/taskService';
import TaskModal from '../components/tasks/TaskModal';
import Button from '../components/ui/Button';
import EmptyState from '../components/ui/EmptyState';
import LoadingState from '../components/ui/LoadingState';
import Select from '../components/ui/Select';
import './TasksPage.css';

const FILTER_TABS = [
  { key: 'ALL', label: 'All Tasks' },
  { key: 'TODAY', label: 'Today & Overdue' },
  { key: 'UPCOMING', label: 'Upcoming' },
  { key: 'COMPLETED', label: 'Completed' },
];

const PRIORITY_FILTER_OPTIONS = [
  { value: 'ALL', label: 'All Priorities' },
  { value: 'CRITICAL', label: 'Critical' },
  { value: 'HIGH', label: 'High' },
  { value: 'MEDIUM', label: 'Medium' },
  { value: 'LOW', label: 'Low' },
];

export const TasksPage = () => {
  const [tasks, setTasks] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const [activeTab, setActiveTab] = useState('ALL');
  const [priorityFilter, setPriorityFilter] = useState('ALL');

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTask, setEditingTask] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [toastMessage, setToastMessage] = useState(null);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 2500);
  };

  const fetchTasks = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await taskService.getTasks();
      setTasks(data || []);
    } catch (err) {
      setError(err.message || 'Failed to load tasks.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTasks();
  }, [fetchTasks]);

  const handleCreateOrUpdateTask = async (payload) => {
    setIsSubmitting(true);
    try {
      if (editingTask) {
        const updated = await taskService.updateTask(editingTask.id, payload);
        setTasks((prev) => prev.map((t) => (t.id === updated.id ? updated : t)));
        showToast('Task updated successfully.');
      } else {
        const created = await taskService.createTask(payload);
        setTasks((prev) => [created, ...prev]);
        showToast('Task created successfully.');
      }
      setIsModalOpen(false);
      setEditingTask(null);
    } catch (err) {
      alert(err.message || 'Failed to save task.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleToggleComplete = async (task) => {
    try {
      const updated = await taskService.completeTask(task.id);
      setTasks((prev) => prev.map((t) => (t.id === updated.id ? updated : t)));
      showToast(updated.status === 'COMPLETED' ? 'Task completed.' : 'Task reopened.');
    } catch (err) {
      alert(err.message || 'Failed to toggle task completion.');
    }
  };

  const handleDeleteTask = async (task) => {
    if (!window.confirm(`Delete "${task.title}"?`)) return;
    try {
      await taskService.deleteTask(task.id);
      setTasks((prev) => prev.filter((t) => t.id !== task.id));
      showToast('Task deleted.');
    } catch (err) {
      alert(err.message || 'Failed to delete task.');
    }
  };

  const openCreateModal = () => {
    setEditingTask(null);
    setIsModalOpen(true);
  };

  const openEditModal = (task) => {
    setEditingTask(task);
    setIsModalOpen(true);
  };

  // Filter tasks based on activeTab and priorityFilter
  const todayStr = useMemo(() => new Date().toISOString().split('T')[0], []);

  const filteredTasks = useMemo(() => {
    return tasks.filter((t) => {
      // Tab filter
      if (activeTab === 'COMPLETED') {
        if (t.status !== 'COMPLETED') return false;
      } else if (activeTab === 'TODAY') {
        if (t.status === 'COMPLETED') return false;
        const isToday = t.dueDate === todayStr;
        const isOverdue = t.status === 'OVERDUE' || (t.dueDate && t.dueDate < todayStr);
        if (!isToday && !isOverdue) return false;
      } else if (activeTab === 'UPCOMING') {
        if (t.status === 'COMPLETED') return false;
        if (!t.dueDate || t.dueDate <= todayStr) return false;
      } else if (activeTab === 'ALL') {
        // Show everything or uncompleted by default? PRD specifies All shows all tasks
      }

      // Priority filter
      if (priorityFilter !== 'ALL' && t.priority !== priorityFilter) {
        return false;
      }

      return true;
    });
  }, [tasks, activeTab, priorityFilter, todayStr]);

  const metrics = useMemo(() => {
    const total = tasks.length;
    const completed = tasks.filter((t) => t.status === 'COMPLETED').length;
    const todayCount = tasks.filter((t) => t.status !== 'COMPLETED' && (t.dueDate === todayStr || t.status === 'OVERDUE')).length;
    const overdueCount = tasks.filter((t) => t.status === 'OVERDUE').length;
    return { total, completed, todayCount, overdueCount };
  }, [tasks, todayStr]);

  if (isLoading && tasks.length === 0) {
    return <LoadingState message="Loading your task inventory..." />;
  }

  return (
    <div className="lifeos-tasks-page">
      {toastMessage && (
        <div className="lifeos-tasks-toast" role="status">
          {toastMessage}
        </div>
      )}

      {/* Header */}
      <header className="lifeos-tasks-page__header">
        <div className="lifeos-tasks-page__header-main">
          <span className="lifeos-tasks-page__eyebrow">Execution Backlog</span>
          <h1 className="lifeos-tasks-page__title">Tasks</h1>
          <p className="lifeos-tasks-page__subline">
            Capture, organize, and execute your actions with clarity and destination.
          </p>
        </div>

        <div className="lifeos-tasks-page__header-actions">
          <Button variant="primary" onClick={openCreateModal}>
            + New Task
          </Button>
        </div>
      </header>

      {/* Metrics Bar */}
      <div className="lifeos-tasks-metrics">
        <div className="lifeos-tasks-metric">
          <span className="lifeos-tasks-metric__label">TOTAL</span>
          <span className="lifeos-tasks-metric__value">{metrics.total}</span>
        </div>
        <div className="lifeos-tasks-metric">
          <span className="lifeos-tasks-metric__label">TODAY & OVERDUE</span>
          <span className="lifeos-tasks-metric__value lifeos-tasks-metric__value--amber">{metrics.todayCount}</span>
        </div>
        {metrics.overdueCount > 0 && (
          <div className="lifeos-tasks-metric">
            <span className="lifeos-tasks-metric__label">CRITICAL OVERDUE</span>
            <span className="lifeos-tasks-metric__value lifeos-tasks-metric__value--crimson">{metrics.overdueCount}</span>
          </div>
        )}
        <div className="lifeos-tasks-metric">
          <span className="lifeos-tasks-metric__label">COMPLETED</span>
          <span className="lifeos-tasks-metric__value lifeos-tasks-metric__value--emerald">{metrics.completed}</span>
        </div>
      </div>

      {/* Toolbar / Filters */}
      <div className="lifeos-tasks-toolbar">
        <div className="lifeos-tasks-tabs" role="tablist">
          {FILTER_TABS.map((tab) => (
            <button
              key={tab.key}
              type="button"
              role="tab"
              aria-selected={activeTab === tab.key}
              className={`lifeos-tasks-tab ${activeTab === tab.key ? 'lifeos-tasks-tab--active' : ''}`}
              onClick={() => setActiveTab(tab.key)}
            >
              {tab.label}
            </button>
          ))}
        </div>

        <div className="lifeos-tasks-filters">
          <Select
            id="filter-priority"
            options={PRIORITY_FILTER_OPTIONS}
            value={priorityFilter}
            onChange={(e) => setPriorityFilter(e.target.value)}
          />
        </div>
      </div>

      {/* Error state */}
      {error && (
        <div className="lifeos-tasks-error" role="alert">
          <p>{error}</p>
          <Button variant="ghost" onClick={fetchTasks}>
            Retry
          </Button>
        </div>
      )}

      {/* Content List */}
      {!error && filteredTasks.length === 0 ? (
        <EmptyState
          title={
            activeTab === 'COMPLETED'
              ? 'No completed tasks yet'
              : activeTab === 'TODAY'
              ? 'No tasks due today'
              : 'Your task backlog is clear'
          }
          message={
            activeTab === 'ALL'
              ? 'Capture your next action and connect it to your strategic goals.'
              : 'Switch filters or create a new task to continue.'
          }
          actionLabel="+ Create Task"
          onAction={openCreateModal}
        />
      ) : (
        <div className="lifeos-tasks-list" role="list">
          {filteredTasks.map((task) => {
            const isCompleted = task.status === 'COMPLETED';
            const isOverdue = task.status === 'OVERDUE' || (task.dueDate && task.dueDate < todayStr && !isCompleted);
            const isDueToday = task.dueDate === todayStr && !isCompleted;

            return (
              <div
                key={task.id}
                role="listitem"
                className={`lifeos-task-row ${isCompleted ? 'lifeos-task-row--completed' : ''} ${
                  isOverdue ? 'lifeos-task-row--overdue' : ''
                }`}
              >
                {/* Checkbox */}
                <button
                  type="button"
                  className={`lifeos-task-row__checkbox ${
                    isCompleted ? 'lifeos-task-row__checkbox--checked' : ''
                  }`}
                  onClick={() => handleToggleComplete(task)}
                  aria-label={isCompleted ? `Reopen task: ${task.title}` : `Complete task: ${task.title}`}
                  aria-pressed={isCompleted}
                >
                  {isCompleted && (
                    <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round">
                      <polyline points="20 6 9 17 4 12" />
                    </svg>
                  )}
                </button>

                {/* Content */}
                <div className="lifeos-task-row__content">
                  <div className="lifeos-task-row__title-line">
                    <span className="lifeos-task-row__title">{task.title}</span>

                    {/* Priority badge */}
                    <span className={`lifeos-priority-badge lifeos-priority-badge--${task.priority?.toLowerCase()}`}>
                      {task.priority}
                    </span>

                    {/* Status badge if Overdue */}
                    {isOverdue && (
                      <span className="lifeos-status-badge lifeos-status-badge--overdue">
                        OVERDUE
                      </span>
                    )}
                    {isDueToday && !isOverdue && (
                      <span className="lifeos-status-badge lifeos-status-badge--today">
                        TODAY
                      </span>
                    )}
                  </div>

                  {task.description && (
                    <p className="lifeos-task-row__description">{task.description}</p>
                  )}

                  {/* Metadata line */}
                  <div className="lifeos-task-row__meta">
                    {task.dueDate && (
                      <span className={`lifeos-task-row__due ${isOverdue ? 'lifeos-task-row__due--overdue' : ''}`}>
                        📅 {task.dueDate}
                      </span>
                    )}

                    {task.estimatedMinutes && (
                      <span className="lifeos-task-row__estimate">
                        ⏱ {task.estimatedMinutes}m
                      </span>
                    )}

                    {task.goalTitle && (
                      <span className="lifeos-task-row__lineage" title="Linked goal">
                        🎯 {task.goalTitle}
                        {task.milestoneTitle && ` → ${task.milestoneTitle}`}
                      </span>
                    )}
                  </div>
                </div>

                {/* Row Actions */}
                <div className="lifeos-task-row__actions">
                  <button
                    type="button"
                    className="lifeos-task-row__action-btn"
                    onClick={() => openEditModal(task)}
                    title="Edit task"
                    aria-label={`Edit ${task.title}`}
                  >
                    Edit
                  </button>
                  <button
                    type="button"
                    className="lifeos-task-row__action-btn lifeos-task-row__action-btn--delete"
                    onClick={() => handleDeleteTask(task)}
                    title="Delete task"
                    aria-label={`Delete ${task.title}`}
                  >
                    Delete
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Task Modal */}
      <TaskModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setEditingTask(null);
        }}
        onSubmit={handleCreateOrUpdateTask}
        task={editingTask}
        isSubmitting={isSubmitting}
      />
    </div>
  );
};

export default TasksPage;
