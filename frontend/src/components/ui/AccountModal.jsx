import React from 'react';
import Modal from './Modal';
import Button from './Button';

export const AccountModal = ({ isOpen, onClose, user, onLogout }) => {
  if (!user) return null;

  const memberSince = user.createdAt
    ? new Date(user.createdAt).toLocaleDateString('en-US', {
        month: 'long',
        day: 'numeric',
        year: 'numeric',
      })
    : 'Active Session';

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="Account"
      description="Your LifeOS authenticated identity."
      size="sm"
    >
      <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-4)' }}>
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: 'var(--space-3)',
            padding: 'var(--space-3)',
            backgroundColor: 'var(--color-bg-surface-subtle)',
            borderRadius: 'var(--radius-md)',
            border: '1px solid var(--color-border-subtle)',
          }}
        >
          <div
            style={{
              width: '40px',
              height: '40px',
              borderRadius: '50%',
              backgroundColor: 'var(--color-accent-subtle)',
              border: '1px solid var(--color-accent-primary)',
              color: 'var(--color-accent-primary)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontWeight: 'var(--font-weight-bold)',
              fontFamily: 'var(--font-sans)',
            }}
          >
            {user.name
              ? user.name
                  .split(' ')
                  .map((p) => p[0])
                  .join('')
                  .toUpperCase()
                  .slice(0, 2)
              : 'U'}
          </div>
          <div>
            <div style={{ fontWeight: 'var(--font-weight-semibold)', color: 'var(--color-text-primary)' }}>
              {user.name}
            </div>
            <div style={{ fontSize: 'var(--font-size-xs)', color: 'var(--color-text-muted)' }}>
              {user.email}
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-2)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 'var(--font-size-sm)' }}>
            <span style={{ color: 'var(--color-text-muted)' }}>Member Since</span>
            <span style={{ color: 'var(--color-text-primary)', fontWeight: 'var(--font-weight-medium)' }}>
              {memberSince}
            </span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 'var(--font-size-sm)' }}>
            <span style={{ color: 'var(--color-text-muted)' }}>Security</span>
            <span style={{ color: 'var(--color-success-text)', fontWeight: 'var(--font-weight-medium)' }}>
              Secure HttpOnly Session
            </span>
          </div>
        </div>

        <div
          style={{
            display: 'flex',
            justifyContent: 'flex-end',
            gap: 'var(--space-2)',
            marginTop: 'var(--space-2)',
            paddingTop: 'var(--space-4)',
            borderTop: '1px solid var(--color-border-subtle)',
          }}
        >
          <Button variant="outline" size="sm" onClick={onClose}>
            Close
          </Button>
          <Button
            variant="danger"
            size="sm"
            onClick={() => {
              onClose();
              if (onLogout) onLogout();
            }}
          >
            Sign out
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default AccountModal;
