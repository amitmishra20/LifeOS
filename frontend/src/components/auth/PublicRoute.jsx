import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../context/useAuth';
import { AUTH_STATUS } from '../../context/authConstants';

export const PublicRoute = () => {
  const { status } = useAuth();

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
      </div>
    );
  }

  if (status === AUTH_STATUS.AUTHENTICATED) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
};

export default PublicRoute;
