package com.bluechat

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext

class BluechatClientManager(
  private val context: ReactApplicationContext,
  private val bluetoothAdapter: BluetoothAdapter,
  private val emitter: BluechatEventEmitters
) {
  private var bluetoothGatt: BluetoothGatt? = null

  @SuppressLint("MissingPermission")
  fun connectToDevice(address: String) {
    val device = bluetoothAdapter.getRemoteDevice(address)
    bluetoothGatt = device.connectGatt(context, false, gattCallback)
    Log.d(BluechatConstants.LOG_TAG, "Connecting to $address")
  }

  @SuppressLint("MissingPermission")
  fun disconnect() {
    bluetoothGatt?.disconnect()

  }

  @SuppressLint("MissingPermission")
  @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
  fun sendMessageToServer(message: String) {
    val service = bluetoothGatt?.getService(BluechatConstants.SERVICE_UUID) ?: return
    val characteristic = service.getCharacteristic(BluechatConstants.MESSAGE_UUID) ?: return

    characteristic.value = message.toByteArray(Charsets.UTF_8)
    bluetoothGatt?.writeCharacteristic(characteristic)
  }

  @SuppressLint("MissingPermission")
  private val gattCallback = object : BluetoothGattCallback() {
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
      super.onConnectionStateChange(gatt, status, newState)

      if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothGatt.STATE_CONNECTED) {
        Log.d(BluechatConstants.LOG_TAG, "Client success and connected")
        bluetoothGatt?.discoverServices()
        emitter.connectionChangeEmitter(
          newState,
          "client",
          gatt?.device?.name ?: gatt?.device?.address
        )
      } else {
        Log.d(BluechatConstants.LOG_TAG, "Client disconnected. Status $status")
        bluetoothGatt?.close()
        bluetoothGatt = null
        emitter.connectionChangeEmitter(newState, "none", null)
      }
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
      super.onServicesDiscovered(gatt, status)

      if (status == BluetoothGatt.GATT_SUCCESS) {
        val service = gatt?.getService(BluechatConstants.SERVICE_UUID)
        val msgCharacteristic = service?.getCharacteristic(BluechatConstants.MESSAGE_UUID)

        if (msgCharacteristic != null) {
          gatt.setCharacteristicNotification(msgCharacteristic, true)
          val descriptor = msgCharacteristic.getDescriptor(BluechatConstants.DESCRIPTOR_UUID)

          if (descriptor != null) {
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
          }

        } else {
          Log.d(BluechatConstants.LOG_TAG, "Characteristic not found")
        }
      }
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onCharacteristicChanged(
      gatt: BluetoothGatt,
      characteristic: BluetoothGattCharacteristic,
      value: ByteArray
    ) {
      super.onCharacteristicChanged(gatt, characteristic, value)

      if (characteristic.uuid == BluechatConstants.MESSAGE_UUID) {
        val message = characteristic.value?.toString((Charsets.UTF_8))

        if (message == BluechatConstants.DISCONNECT_MSG) {
          disconnect()
        }

        Log.d(BluechatConstants.LOG_TAG, "Message Received $message")
        message?.let { emitter.onMessageSendEmitter(it) }
      }
    }
  }
}
