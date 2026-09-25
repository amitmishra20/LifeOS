import React from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/useAuth';
import { AUTH_STATUS } from '../../context/authConstants';

export const ProtectedRoute = () => {
  const { status } = useAuth();
  const location = useLocation();

  if (status === AUTH_STATUS.LOADING) {
    return (
      <div
        style={{
          minHeight: '100vh',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          backgroundColor: 'var(--color-bg-base)',
          color: 'var(--color-text-secondary)',
          gap: 'var(--space-4)',
        }}
        aria-busy="true"
        aria-live="polite"
      >
        <div
          style={{
            width: '32px',
            height: '32px',
            border: '2px solid var(--color-border-default)',
            borderTopColor: 'var(--color-accent-primary)',
            borderRadius: '50%',
            animation: 'lifeos-spin 0.8s linear infinite',
          }}
        />
        <style>{`
          @keyframes lifeos-spin {
            to { transform: rotate(360deg); }
          }
        `}</style>
        <span style={{ fontSize: 'var(--font-size-sm)', letterSpacing: 'var(--tracking-wide)' }}>
          Entering LifeOS...
        </span>
      </div>
    );
  }

  if (status === AUTH_STATUS.UNAUTHENTICATED) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return <Outlet />;
};

export default ProtectedRoute;
