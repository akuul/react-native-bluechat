# React Native Bluechat

[![mit licence][license-badge]][license] ![Supported Platforms](https://img.shields.io/badge/platforms-android-ios?style=for-the-badge)

### Simple one-on-one bluetooth chatting module using Bluetooth Low Energy (BLE)

This library was created as part of a learning exercise to explore and expose native Android events to JS using Turbo Modules.  
Initial usage was meant for during-flight communication through Bluetooth.  
It only supporst **New Architecture** and backward compatability will not be implemented.

> [!WARNING]
> **Android only:** This library currently supports Android only. There are no plans for iOS support at this time.  
> This library is **not suitable** for production use. It is intended as a refererence implementation.

## Features

- Advertise your device
- Scan for nearby BLE devices
- Connect to a selected BLE device
- Send and receive messages between connected devices

## Getting started

```sh
npm i react-native-bluechat
```

```sh
yarn add react-native-bluechat
```

```sh
bun add react-native-bluechat
```

## Setup Android

Add required permission to your `AndroidManifest.xml` file.

```xml
    <!-- Request legacy Bluetooth permissions on older devices. -->
    <uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
    <!-- For Android 12 (API 31) or higher -->
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN"/>

    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

You can use [react-native-permissions](https://github.com/zoontek/react-native-permissions) or similar for requesting permissions.

## Usage

**Server**: Device that starts GATT Server. This is a device that calls `startAdvertising()` method.  
**Client**: Device that connects to GATT Server. A devices that scans using `startScan()` method and connects to a scanned device using `connectToDevice(address)` method.

Usual flow consists of

- **Device A** that wants to act as GATT server calls `startAdvertising()` method. This method will make a device discoverable by others.
- **Device B** starts scanning for devices using `startsScan()` and `onBluetoothScanListener` start emitting devices on the same `SERVICE_UUID`.
- `onConnectionChangedListener` will emit GATT Connection change to determine connection state as well as role (client or server).
- Based on your role, devices call `sendMessageToServer(message)` for client or `sendMessageToClient(messages)` for server.
- `onMessageReceivedListener` listens for messages comming from other device. This means you are not receiving your own sent messages.
- Finally, `disconnectFromDevice` will disconnect both client and server, as well as trigger `onConnectionChangedListener` marking as `STATE_DISCONNECTED`.

You can read more about [Bluetooth Low Energy on Android docs](https://developer.android.com/develop/connectivity/bluetooth/ble/ble-overview)

```tsx
//Hook
import { useBluechat } from 'react-native-bluechat';

// ...

const {
  startScan,
  stopScan,
  startAdvertising,
  stopAdvertising,
  connectToDevice,
  disconnectFromDevice,
  btEnabled,
  isScanning,
  sendMessage,
  scannedDevices,
  messages,
  connectionState,
  isAdvertising,
} = useBluechat();

// ...
```

See [Example](./example/src/App.tsx)

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made as learning process by [Aironas Kulvelis](https://www.linkedin.com/in/aironas-kulvelis-633901173/)

[license-badge]: https://img.shields.io/badge/LICENSE-MIT-green?style=for-the-badge
[license]: https://github.com/akuul/react-native-bluechat/blob/main/LICENSE
