import { createSeedDataset } from './seedDataset';

/**
 * LifeOS Authoritative Seed Dataset Entry Point
 * Exclusively serves the single approved CS-student narrative (Amit).
 * Multi-persona simulator removed.
 */
export const getInitialDataset = () => {
  return createSeedDataset();
};

export default getInitialDataset;
