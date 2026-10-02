export const isDesignPreviewActive = () => {
  if (!import.meta.env.DEV || typeof window === 'undefined') return false;
  return new URLSearchParams(window.location.search).get('designPreview') === 'true';
};
