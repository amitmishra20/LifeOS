import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/useAuth';
import userService from '../services/userService';
import Button from '../components/ui/Button';
import LoadingState from '../components/ui/LoadingState';
import Modal from '../components/ui/Modal';
import './SettingsPage.css';

export const SettingsPage = () => {
  const navigate = useNavigate();
  const { logout } = useAuth();

  const [profile, setProfile] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  // Deletion Modal State
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [confirmText, setConfirmText] = useState('');
  const [isDeleting, setIsDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState(null);

  const fetchProfile = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await userService.getProfileSummary();
      setProfile(data);
    } catch (err) {
      setError(err.message || 'Failed to load profile summary.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchProfile();
  }, [fetchProfile]);

  const handleDeleteAccount = async () => {
    if (confirmText !== 'DELETE') return;

    setIsDeleting(true);
    setDeleteError(null);
    try {
      await userService.deleteAccount();
      if (logout) {
        await logout();
      }
      navigate('/login', { replace: true });
    } catch (err) {
      setDeleteError(err.message || 'Failed to delete account. Please try again.');
      setIsDeleting(false);
    }
  };

  const memberSinceFormatted = profile?.createdAt
    ? new Date(profile.createdAt).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
      })
    : 'Unknown';

  if (isLoading && !profile) {
    return <LoadingState message="Loading account settings and data overview..." />;
  }

  return (
    <div className="lifeos-settings-page">
      <header className="lifeos-settings-header">
        <span className="lifeos-settings-kicker">ACCOUNT & PREFERENCES</span>
        <h1 className="lifeos-settings-title">Settings</h1>
        <p className="lifeos-settings-subline">
          Manage your account credentials, data footprint, and system lifecycle.
        </p>
      </header>

      {error && (
        <div className="lifeos-settings-error" role="alert">
          <p>{error}</p>
          <Button variant="ghost" onClick={fetchProfile}>
            Retry
          </Button>
        </div>
      )}

      {profile && (
        <div className="lifeos-settings-content">
          {/* Section 1: Profile Summary */}
          <section className="lifeos-settings-section" aria-labelledby="profile-heading">
            <h2 id="profile-heading" className="lifeos-settings-section__title">
              Profile Summary
            </h2>
            <div className="lifeos-settings-card">
              <div className="lifeos-settings-field">
                <span className="lifeos-settings-field__label">Full Name</span>
                <span className="lifeos-settings-field__value">{profile.name}</span>
              </div>
              <div className="lifeos-settings-field">
                <span className="lifeos-settings-field__label">Email Address</span>
                <span className="lifeos-settings-field__value">{profile.email}</span>
              </div>
              <div className="lifeos-settings-field">
                <span className="lifeos-settings-field__label">Member Since</span>
                <span className="lifeos-settings-field__value">{memberSinceFormatted}</span>
              </div>
            </div>
          </section>

          {/* Section 2: Data Overview */}
          <section className="lifeos-settings-section" aria-labelledby="data-heading">
            <h2 id="data-heading" className="lifeos-settings-section__title">
              Data Footprint
            </h2>
            <p className="lifeos-settings-section__sub">
              Total stored personal productivity assets in your LifeOS database.
            </p>
            <div className="lifeos-settings-stats-grid">
              <div className="lifeos-settings-stat-card">
                <span className="lifeos-settings-stat__count">{profile.totalGoals}</span>
                <span className="lifeos-settings-stat__label">Goals</span>
              </div>
              <div className="lifeos-settings-stat-card">
                <span className="lifeos-settings-stat__count">{profile.totalTasks}</span>
                <span className="lifeos-settings-stat__label">Tasks</span>
              </div>
              <div className="lifeos-settings-stat-card">
                <span className="lifeos-settings-stat__count">{profile.totalHabits}</span>
                <span className="lifeos-settings-stat__label">Habits</span>
              </div>
              <div className="lifeos-settings-stat-card">
                <span className="lifeos-settings-stat__count">{profile.totalLearningItems}</span>
                <span className="lifeos-settings-stat__label">Learning Items</span>
              </div>
              <div className="lifeos-settings-stat-card">
                <span className="lifeos-settings-stat__count">{profile.totalNotes}</span>
                <span className="lifeos-settings-stat__label">Notes</span>
              </div>
            </div>
          </section>

          {/* Section 3: Danger Zone */}
          <section className="lifeos-settings-section lifeos-settings-section--danger" aria-labelledby="danger-heading">
            <h2 id="danger-heading" className="lifeos-settings-section__title lifeos-danger-title">
              Danger Zone
            </h2>
            <div className="lifeos-settings-card lifeos-settings-card--danger">
              <div className="lifeos-danger-info">
                <strong>Permanently Delete Account</strong>
                <p>
                  Permanently erase your account and all associated goals, tasks, habits, learning progress,
                  calendar events, notes, and productivity metrics. This action is irreversible.
                </p>
              </div>
              <Button
                variant="primary"
                className="lifeos-button--danger"
                onClick={() => {
                  setConfirmText('');
                  setDeleteError(null);
                  setIsDeleteModalOpen(true);
                }}
              >
                Delete Account
              </Button>
            </div>
          </section>
        </div>
      )}

      {/* Account Deletion Confirmation Modal */}
      <Modal
        isOpen={isDeleteModalOpen}
        onClose={() => !isDeleting && setIsDeleteModalOpen(false)}
        title="Confirm Account Deletion"
      >
        <div className="lifeos-delete-modal-content">
          <p className="lifeos-delete-warning">
            This action <strong>CANNOT</strong> be undone. It will permanently purge your account and all
            associated personal data across all LifeOS systems.
          </p>

          {deleteError && (
            <div className="lifeos-delete-modal-error" role="alert">
              {deleteError}
            </div>
          )}

          <div className="lifeos-delete-input-group">
            <label htmlFor="confirm-delete-input">
              To confirm, type <strong className="lifeos-highlight-delete">DELETE</strong> in the box below:
            </label>
            <input
              id="confirm-delete-input"
              type="text"
              className="lifeos-delete-input"
              value={confirmText}
              onChange={(e) => setConfirmText(e.target.value)}
              placeholder="DELETE"
              disabled={isDeleting}
              autoFocus
            />
          </div>

          <div className="lifeos-delete-modal-actions">
            <Button
              variant="ghost"
              onClick={() => setIsDeleteModalOpen(false)}
              disabled={isDeleting}
            >
              Cancel
            </Button>
            <Button
              variant="primary"
              className="lifeos-button--danger"
              disabled={confirmText !== 'DELETE' || isDeleting}
              onClick={handleDeleteAccount}
            >
              {isDeleting ? 'Deleting Data...' : 'Permanently Delete Account'}
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default SettingsPage;
