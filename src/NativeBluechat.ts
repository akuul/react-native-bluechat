import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';
import type { EventEmitter } from 'react-native/Libraries/Types/CodegenTypes';

export type BluetoothStateValues = 'OFF' | 'TURNING_OFF' | 'ON' | 'TURNING_ON';

export type BluetoothState = {
  state: BluetoothStateValues;
};

export type BluetoothDevice = {
  name: string;
  address: string;
};

export type MessageReceiver = {
  value: string;
};

export type AdvertiseCallback = {
  code: number;
};

export type ConnectionChange = {
  state: number;
  role: 'client' | 'server' | 'none';
  connectedDevice?: string;
};

export interface Spec extends TurboModule {
  /**
   * Check if Bluetooth is currently enabled
   * @returns `true` if Bluetooth is ON, otherwise `false`
   */
  isEnabled(): boolean;

  /**
   * Start scanning for nearby BLE devices
   * Results will be emitted via `onBluetoothScan` event
   */
  startScan(): void;

  /**
   * Stop scanning for BLE devices
   */
  stopScan(): void;

  /**
   * Start advertising and open GATT server
   *
   * The advertising device becomes the "server", and others can connect as clients.
   *
   * @returns A promise resolving to an integer status code
   */
  startAdvertise(): Promise<number>;

  /**
   * Stop advertising and close the GATT server
   */
  stopAdvertise(): void;

  /**
   * Connect to a scanned BLE device by address
   *
   * @param address - The MAC address of the device to connect to
   */
  connectToDevice(address: string): void;

  /**
   * Disconnect from the currently connected device
   */
  disconnectFromDevice(): void;

  /**
   * Send a message to the server (only if acting as a client)
   *
   * @param message - The string message to send
   */
  sendMessageToServer(message: string): void;

  /**
   * Send a message to the connected client (only if acting as a server)
   *
   * @param message - The string message to send
   */
  sendMessageToClient(message: string): void;

  /**
   * Emits whenever the Bluetooth state changes (ON, OFF, etc.)
   */
  readonly onBluetoothStateChanged: EventEmitter<BluetoothState>;

  /**
   * Emits each time a BLE device is found during scanning
   */
  readonly onBluetoothScan: EventEmitter<BluetoothDevice>;

  /**
   * Emits when a message is received from the connected device
   */
  readonly onMessageReceived: EventEmitter<MessageReceiver>;

  /**
   * Emits on connection state change with role and connected device info
   */
  readonly onConnectionChanged: EventEmitter<ConnectionChange>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('Bluechat');
