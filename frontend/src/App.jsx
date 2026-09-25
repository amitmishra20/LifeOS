import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/auth/ProtectedRoute';
import PublicRoute from './components/auth/PublicRoute';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import AppLayout from './layouts/AppLayout';
import DashboardPage from './pages/DashboardPage';
import ModulePlaceholderPage from './pages/ModulePlaceholderPage';

export function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public Authentication Routes */}
          <Route element={<PublicRoute />}>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
          </Route>

          {/* Protected Application Routes */}
          <Route element={<ProtectedRoute />}>
            <Route element={<AppLayout />}>
              {/* LifeOS Core Product Surface */}
              <Route path="/" element={<DashboardPage />} />

              {/* Upcoming Phase Modules */}
              <Route path="/focus/*" element={<ModulePlaceholderPage />} />
              <Route path="/plan/*" element={<ModulePlaceholderPage />} />
              <Route path="/grow/*" element={<ModulePlaceholderPage />} />
              <Route path="/reflect/*" element={<ModulePlaceholderPage />} />
              <Route path="/capture/*" element={<ModulePlaceholderPage />} />

              {/* Fallback to Dashboard */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Route>
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
