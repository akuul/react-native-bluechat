import { useEffect } from 'react';
import { PERMISSIONS, requestMultiple } from 'react-native-permissions';

export const usePermissions = () => {
  useEffect(() => {
    requestMultiple([
      PERMISSIONS.ANDROID.BLUETOOTH_CONNECT,
      PERMISSIONS.ANDROID.BLUETOOTH_SCAN,
      PERMISSIONS.ANDROID.BLUETOOTH_ADVERTISE,
      PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION,
    ]);
  }, []);
};
