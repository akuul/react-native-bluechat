import {
  ActivityIndicator,
  FlatList,
  Pressable,
  StyleSheet,
  Text,
} from 'react-native';
import type { BluetoothDevice } from '../../../src/NativeBluechat';

interface DevicesListProps {
  connectToDevice: (address: string) => void;
  scannedDevices: BluetoothDevice[];
}

export const DevicesList = ({
  scannedDevices,
  connectToDevice,
}: DevicesListProps) => {
  return (
    <FlatList
      data={scannedDevices}
      ListHeaderComponent={() => <ActivityIndicator />}
      renderItem={({ item }) => (
        <Pressable
          onPress={() => connectToDevice(item.address)}
          style={styles.item}
        >
          <Text>{item.name}</Text>
        </Pressable>
      )}
    />
  );
};

const styles = StyleSheet.create({
  item: {
    padding: 8,
    backgroundColor: 'grey',
    margin: 4,
  },
});
