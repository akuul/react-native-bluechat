export const AdvertiseCallbackErrorCodes = {
  1: 'ADVERTISE_FAILED_DATA_TOO_LARGE',
  2: 'ADVERTISE_FAILED_TOO_MANY_ADVERTISERS',
  3: 'ADVERTISE_FAILED_ALREADY_STARTED',
  4: 'ADVERTISE_FAILED_INTERNAL_ERROR',
  5: 'ADVERTISE_FAILED_FEATURE_UNSUPPORTED',
} as const;

export const GattConnectionStates = {
  0: 'STATE_DISCONNECTED',
  2: 'STATE_CONNECTED',
} as const;

export const ChatDirection = {
  LEFT: 'left',
  RIGHT: 'right',
} as const;

export const Role = {
  none: 'none',
  client: 'client',
  server: 'server',
} as const;
