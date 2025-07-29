import { useEffect, useState } from 'react';
import { AdvertiseCallbackErrorCodes, ChatDirection, Role } from '../constants';
import {
  onBluetoothScanListener,
  onBluetoothStateChangeListener,
  onConnectionChangedListener,
  onMessageReceivedListener,
} from '../eventEmitters';
import Bluechat, { type BluetoothDevice } from '../NativeBluechat';
import {
  type AdvertiseErrorCode,
  type AdvertiseResult,
  type BluechatMessage,
  type GattConnectionChangeCallback,
} from '../types';

const initialConnectionState: GattConnectionChangeCallback = {
  state: 'STATE_DISCONNECTED',
  role: Role.none,
  connectedDevice: undefined,
};

export const useBluechat = () => {
  const [scannedDevices, setScannedDevices] = useState<BluetoothDevice[]>([]);
  const [messages, setMessages] = useState<BluechatMessage[]>([]);
  const [btEnabled, setBtEnabled] = useState(Bluechat.isEnabled());
  const [isScanning, setIsScanning] = useState(false);
  const [isAdvertising, setIsAdvertising] = useState(false);
  const [connectionState, setConnectionState] =
    useState<GattConnectionChangeCallback>(initialConnectionState);

  const startScan = () => {
    Bluechat.startScan();
    setIsScanning(true);
  };

  const stopScan = () => {
    Bluechat.stopScan();
    setIsScanning(false);
  };

  const startAdvertising = async (): Promise<AdvertiseResult> => {
    const res = await Bluechat.startAdvertise();
    if (res === 0) {
      setIsAdvertising(true);
      return { result: 'success' };
    }
    const error =
      AdvertiseCallbackErrorCodes[res as AdvertiseErrorCode] ?? 'UNKNOWN_ERROR';

    return { result: 'failed', error };
  };

  const stopAdvertising = () => {
    setIsAdvertising(false);
    Bluechat.stopAdvertise();
  };

  const connectToDevice = (address: string) => {
    if (isScanning) {
      stopScan();
      setScannedDevices([]);
    }
    Bluechat.connectToDevice(address);
  };

  const disconnectFromDevice = () => {
    Bluechat.disconnectFromDevice();
  };

  const sendMessage = (msg: string) => {
    if (connectionState.role === Role.none) return;

    const newMessage = {
      value: msg,
      direction: ChatDirection.RIGHT,
    } satisfies BluechatMessage;

    if (connectionState.role === Role.client) {
      Bluechat.sendMessageToServer(msg);
    } else {
      Bluechat.sendMessageToClient(msg);
    }

    setMessages((prev) => [...prev, newMessage]);
  };

  const clearScannedDevices = () => {
    setScannedDevices([]);
  };

  const clearMessages = () => {
    setMessages([]);
  };

  useEffect(() => {
    const unsubBt = onBluetoothStateChangeListener((e) => {
      setBtEnabled(e.state === 'ON');
    });

    const unsubConnection = onConnectionChangedListener((e) => {
      console.log(e);
      if (e.state === 'STATE_DISCONNECTED') {
        setMessages([]);
        setScannedDevices([]);
      }

      setConnectionState(e);
    });

    return () => {
      unsubBt.remove();
      unsubConnection.remove();
    };
  }, []);

  useEffect(() => {
    const unsubMessagesListener = onMessageReceivedListener((e) => {
      const newMessage: BluechatMessage = {
        value: e.value,
        direction: ChatDirection.LEFT,
      };

      setMessages((prev) => [...prev, newMessage]);
    });

    return () => unsubMessagesListener.remove();
  }, [messages]);

  useEffect(() => {
    const unsubScanListener = onBluetoothScanListener((e) => {
      const exists = scannedDevices.find((d) => d.address === e.address);
      if (!exists) {
        setScannedDevices((prev) => [...prev, e]);
      }
    });

    return () => unsubScanListener.remove();
  }, [scannedDevices]);

  return {
    btEnabled,
    isScanning,
    startScan,
    stopScan,
    startAdvertising,
    stopAdvertising,
    connectToDevice,
    disconnectFromDevice,
    sendMessage,
    scannedDevices,
    connectionState,
    messages,
    clearScannedDevices,
    clearMessages,
    isAdvertising,
  };
};
