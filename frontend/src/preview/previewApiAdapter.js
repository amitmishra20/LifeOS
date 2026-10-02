import { isDesignPreviewActive } from './previewMode';
import { previewStore } from './previewStore';

export const previewApiAdapter = async (config) => {
  const pathname = new URL(config.url || '', window.location.origin).pathname;
  return {
    data: previewStore.read(pathname),
    status: 200,
    statusText: 'OK',
    headers: { 'content-type': 'application/json' },
    config,
    request: null,
  };
};

export const getPreviewAdapter = () => (isDesignPreviewActive() ? previewApiAdapter : undefined);
