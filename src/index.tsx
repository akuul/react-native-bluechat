export { useBluechat } from './hook';

export {
  onBluetoothScanListener,
  onBluetoothStateChangeListener,
  onConnectionChangedListener,
  onMessageReceivedListener,
} from './eventEmitters';

export {
  AdvertiseCallbackErrorCodes,
  GattConnectionStates,
  ChatDirection,
  Role,
} from './constants';

export type { BluechatMessage, GattConnectionChangeCallback } from './types';

export { default as BlueChat } from './NativeBluechat';
