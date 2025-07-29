# React Native Bluechat

[![mit licence][license-badge]][license] ![Supported Platforms](https://img.shields.io/badge/platforms-android-ios?style=for-the-badge)

### Simple one-on-one bluetooh chatting module using Bluetooth Low Energy (BLE)

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
