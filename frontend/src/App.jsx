import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/auth/ProtectedRoute';
import PublicRoute from './components/auth/PublicRoute';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import AppLayout from './layouts/AppLayout';
import DashboardPage from './pages/DashboardPage';
import GoalsPage from './pages/GoalsPage';
import GoalDetailPage from './pages/GoalDetailPage';
import TasksPage from './pages/TasksPage';
import FocusPage from './pages/FocusPage';
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

              {/* Execution & Focus Domains */}
              <Route path="/focus" element={<FocusPage />} />
              <Route path="/tasks" element={<TasksPage />} />

              {/* Strategic Goals & Life Map Domain */}
              <Route path="/goals" element={<GoalsPage />} />
              <Route path="/goals/:id" element={<GoalDetailPage />} />
              <Route path="/plan/goals" element={<Navigate to="/goals" replace />} />

              {/* Upcoming Phase Modules */}
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
