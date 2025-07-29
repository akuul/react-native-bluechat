import { StatusBar, StyleSheet, Text, View } from 'react-native';
import { useBluechat } from 'react-native-bluechat';
import { ConnectedArea } from './components/ConnectedArea';
import { DevicesList } from './components/DevicesList';
import { StartArea } from './components/StartArea';
import { usePermissions } from './usePermissions';

export default function App() {
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

  usePermissions();

  if (!btEnabled) {
    return (
      <View style={styles.container}>
        <Text style={styles.header}>Bluetooth is not enabled</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <StatusBar barStyle="dark-content" />
      {connectionState.state === 'STATE_CONNECTED' ? (
        <ConnectedArea
          connectionState={connectionState}
          disconnectFromDevice={disconnectFromDevice}
          messages={messages}
          sendMessage={sendMessage}
        />
      ) : (
        <>
          <StartArea
            isAdvertising={isAdvertising}
            isScanning={isScanning}
            startScan={startScan}
            stopScan={stopScan}
            startAdvertising={startAdvertising}
            stopAdvertising={stopAdvertising}
          />
          {isScanning && (
            <DevicesList
              connectToDevice={connectToDevice}
              scannedDevices={scannedDevices}
            />
          )}
        </>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    paddingTop: 100,
    width: '100%',
    paddingHorizontal: 24,
    gap: 16,
    flex: 1,
    backgroundColor: 'white',
  },
  textInput: {
    color: 'red',
    borderWidth: 1,
    width: '80%',
    paddingHorizontal: 30,
  },
  header: {
    fontSize: 30,
    fontWeight: 'bold',
  },
});
