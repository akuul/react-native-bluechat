import { Button, StyleSheet, Text, View } from 'react-native';
import type { AdvertiseResult } from '../../../src/types';

interface StartAreaProps {
  isScanning: boolean;
  isAdvertising: boolean;
  startScan: () => void;
  stopScan: () => void;
  startAdvertising: () => Promise<AdvertiseResult>;
  stopAdvertising: () => void;
}

export const StartArea = ({
  startAdvertising,
  stopAdvertising,
  isScanning,
  isAdvertising,
  startScan,
  stopScan,
}: StartAreaProps) => {
  const onAdvertisingPress = async () => {
    if (isAdvertising) {
      stopAdvertising();
    } else {
      await startAdvertising();
    }
  };

  const onScanPress = () => {
    if (isScanning) {
      stopScan();
    } else {
      startScan();
    }
  };

  return (
    <View style={styles.container}>
      <Button
        disabled={isAdvertising}
        title={isScanning ? 'Stop Scan' : 'Start Scan'}
        onPress={onScanPress}
      />
      <Text>or</Text>
      <Button
        disabled={isScanning}
        title={`${isAdvertising ? 'Stop' : 'Start'} advertising your device`}
        onPress={onAdvertisingPress}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    gap: 20,
  },
});
