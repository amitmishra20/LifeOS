import React from 'react';
import {
  IconDashboard,
  IconTodayFocus,
  IconTasks,
  IconHabits,
  IconGoals,
  IconCalendar,
  IconLearning,
  IconProgress,
  IconAnalytics,
  IconGoalHealth,
  IconRecommendations,
  IconNotes,
} from '../ui/Icons';

export const NAV_GROUPS = [
  {
    title: null,
    items: [
      { label: 'Home', path: '/', icon: <IconDashboard /> },
    ],
  },
  {
    title: 'FOCUS',
    items: [
      { label: "Today's Focus", path: '/focus', icon: <IconTodayFocus /> },
      { label: 'Tasks', path: '/tasks', icon: <IconTasks /> },
      { label: 'Habits', path: '/habits', icon: <IconHabits /> },
    ],
  },
  {
    title: 'PLAN',
    items: [
      { label: 'Goals', path: '/goals', icon: <IconGoals /> },
      { label: 'Calendar', path: '/calendar', icon: <IconCalendar /> },
      { label: 'Life Map', path: '/goals', icon: <IconGoalHealth /> },
    ],
  },
  {
    title: 'GROW',
    items: [
      { label: 'Learning', path: '/learning', icon: <IconLearning /> },
      { label: 'Progress', path: '/progress', icon: <IconProgress /> },
    ],
  },
  {
    title: 'CAPTURE',
    items: [
      { label: 'Notes', path: '/notes', icon: <IconNotes /> },
    ],
  },
  {
    title: 'REFLECT',
    items: [
      { label: 'Analytics', path: '/analytics', icon: <IconAnalytics /> },
      { label: 'Goal Health', path: '/goal-health', icon: <IconGoalHealth /> },
      { label: 'Recommendations', path: '/recommendations', icon: <IconRecommendations /> },
    ],
  },
];
