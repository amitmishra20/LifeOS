import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import AppLayout from './layouts/AppLayout';
import DashboardPage from './pages/DashboardPage';
import FoundationPage from './pages/FoundationPage';
import ModulePlaceholderPage from './pages/ModulePlaceholderPage';

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          {/* LifeOS Core Product Surface */}
          <Route path="/" element={<DashboardPage />} />

          {/* Technical Diagnostics Surface preserved from Phase 1 */}
          <Route path="/dev/foundation" element={<FoundationPage />} />

          {/* Upcoming Phase Modules */}
          <Route path="/focus/*" element={<ModulePlaceholderPage />} />
          <Route path="/plan/*" element={<ModulePlaceholderPage />} />
          <Route path="/grow/*" element={<ModulePlaceholderPage />} />
          <Route path="/reflect/*" element={<ModulePlaceholderPage />} />
          <Route path="/capture/*" element={<ModulePlaceholderPage />} />

          {/* Fallback to Dashboard */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
