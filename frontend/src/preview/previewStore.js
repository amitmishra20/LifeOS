import { previewData } from './previewData';

const clone = (value) => JSON.parse(JSON.stringify(value));

export const previewStore = {
  read(pathname) {
    if (pathname.endsWith('/auth/me')) return clone(previewData.user);
    if (pathname.endsWith('/auth/csrf')) return { token: 'design-preview-csrf' };
    if (pathname.includes('/goals')) return clone(previewData.goals);
    if (pathname.includes('/tasks')) return clone(previewData.tasks);
    if (pathname.includes('/habits')) return clone(previewData.habits);
    if (pathname.includes('/recommendations') || pathname.includes('/daily-focus')) return clone(previewData.recommendations);
    return {};
  },
};
