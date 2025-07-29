package com.bluechat

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService

object BluechatGattFactory {
  fun createService(): BluetoothGattService {

    val service = BluetoothGattService(
      BluechatConstants.SERVICE_UUID,
      BluetoothGattService.SERVICE_TYPE_PRIMARY
    )

    val messageCharacteristic = BluetoothGattCharacteristic(
      BluechatConstants.MESSAGE_UUID,
      BluetoothGattCharacteristic.PROPERTY_READ or
        BluetoothGattCharacteristic.PROPERTY_WRITE or
        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
      BluetoothGattCharacteristic.PERMISSION_READ or
        BluetoothGattCharacteristic.PERMISSION_WRITE
    )

    val descriptor = BluetoothGattDescriptor(
      BluechatConstants.DESCRIPTOR_UUID,
      BluetoothGattDescriptor.PERMISSION_READ or BluetoothGattDescriptor.PERMISSION_WRITE
    )

    messageCharacteristic.addDescriptor(descriptor)
    service.addCharacteristic(messageCharacteristic)

    return service
  }
}
