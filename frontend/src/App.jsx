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
import HabitsPage from './pages/HabitsPage';
import FocusPage from './pages/FocusPage';
import LearningPage from './pages/LearningPage';
import CalendarPage from './pages/CalendarPage';
import NotesPage from './pages/NotesPage';
import AnalyticsPage from './pages/AnalyticsPage';
import GoalHealthPage from './pages/GoalHealthPage';
import RecommendationsPage from './pages/RecommendationsPage';
import SettingsPage from './pages/SettingsPage';
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
              <Route path="/habits" element={<HabitsPage />} />

              {/* Strategic Goals & Life Map Domain */}
              <Route path="/goals" element={<GoalsPage />} />
              <Route path="/goals/:id" element={<GoalDetailPage />} />
              <Route path="/plan/goals" element={<Navigate to="/goals" replace />} />

              {/* Planning & Schedule Domain */}
              <Route path="/calendar" element={<CalendarPage />} />
              <Route path="/plan/calendar" element={<Navigate to="/calendar" replace />} />

              {/* Growth & Mastery Domain */}
              <Route path="/learning" element={<LearningPage />} />
              <Route path="/grow/learning" element={<Navigate to="/learning" replace />} />
              <Route path="/progress" element={<Navigate to="/analytics" replace />} />
              <Route path="/grow/progress" element={<Navigate to="/analytics" replace />} />

              {/* Knowledge & Capture Domain */}
              <Route path="/notes" element={<NotesPage />} />
              <Route path="/capture/notes" element={<Navigate to="/notes" replace />} />

              {/* Reflection & Analytics Domain */}
              <Route path="/analytics" element={<AnalyticsPage />} />
              <Route path="/reflect/analytics" element={<Navigate to="/analytics" replace />} />
              <Route path="/goal-health" element={<GoalHealthPage />} />
              <Route path="/reflect/goal-health" element={<Navigate to="/goal-health" replace />} />
              <Route path="/recommendations" element={<RecommendationsPage />} />
              <Route path="/reflect/recommendations" element={<Navigate to="/recommendations" replace />} />
              <Route path="/settings" element={<SettingsPage />} />
              <Route path="/reflect/settings" element={<Navigate to="/settings" replace />} />

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
