import type {
  AdvertiseCallbackErrorCodes,
  ChatDirection,
  GattConnectionStates,
  Role,
} from './constants';

export interface GattConnectionChangeCallback {
  state: GattConnectionStatesMessage;
  role: (typeof Role)[keyof typeof Role];
  connectedDevice?: string;
}

export interface BluechatMessage {
  value: string;
  direction: (typeof ChatDirection)[keyof typeof ChatDirection];
}

export type GattConnectionStatesMessage =
  | 'STATE_DISCONNECTED'
  | 'STATE_CONNECTED';

export type AdvertiseErrorMessage =
  | 'ADVERTISE_FAILED_DATA_TOO_LARGE'
  | 'ADVERTISE_FAILED_TOO_MANY_ADVERTISERS'
  | 'ADVERTISE_FAILED_ALREADY_STARTED'
  | 'ADVERTISE_FAILED_INTERNAL_ERROR'
  | 'ADVERTISE_FAILED_FEATURE_UNSUPPORTED'
  | 'UNKNOWN_ERROR';

export type AdvertiseResult =
  | { result: 'success' }
  | { result: 'failed'; error: AdvertiseErrorMessage };

export type GattConnectionCode = keyof typeof GattConnectionStates;

export type AdvertiseErrorCode = keyof typeof AdvertiseCallbackErrorCodes;
