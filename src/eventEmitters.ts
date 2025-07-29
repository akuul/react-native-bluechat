import { GattConnectionStates } from './constants';
import NativeBluechat, {
  type BluetoothDevice,
  type BluetoothState,
  type MessageReceiver,
} from './NativeBluechat';
import type { GattConnectionChangeCallback, GattConnectionCode } from './types';

export const onBluetoothStateChangeListener = (
  callback: (state: BluetoothState) => void
) => NativeBluechat.onBluetoothStateChanged(callback);

export const onBluetoothScanListener = (
  callback: (device: BluetoothDevice) => void
) => NativeBluechat.onBluetoothScan(callback);

export const onConnectionChangedListener = (
  callback: (state: GattConnectionChangeCallback) => void
) =>
  NativeBluechat.onConnectionChanged((state) => {
    const mappedState = {
      ...state,
      state:
        GattConnectionStates[state.state as GattConnectionCode] ??
        'UNKNOWN_STATE',
    };
    callback(mappedState);
  });

export const onMessageReceivedListener = (
  callback: (message: MessageReceiver) => void
) => NativeBluechat.onMessageReceived(callback);
