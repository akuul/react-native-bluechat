package com.bluechat

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import okhttp3.internal.http2.Http2Reader

class BluechatServerManager(
  private val context: ReactApplicationContext,
  private val bluetoothManager: BluetoothManager,
  private val emitter: BluechatEventEmitters
) {
  private var gattServer: BluetoothGattServer? = null
  private val advertiser: BluetoothLeAdvertiser? = bluetoothManager.adapter.bluetoothLeAdvertiser
  private var advertisePromise: Promise? = null
  private var connectedDevice: BluetoothDevice? = null

  private val gattService: BluetoothGattService = BluechatGattFactory.createService()

  @SuppressLint("MissingPermission")
  fun startServer(promise: Promise?) {
    advertisePromise = promise

    Log.d(BluechatConstants.LOG_TAG, "Starting GATT server and Advertising")
    gattServer =
      bluetoothManager.openGattServer(context, serverCallback).apply { addService((gattService)) }
    startAdvertising()
  }

  @SuppressLint("MissingPermission")
  fun stopServer() {
    Log.d(BluechatConstants.LOG_TAG, "Stopping GATT server and Advertising")
    gattServer?.close()
    gattServer = null
    stopAdvertising()
  }

  @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
  @SuppressLint("MissingPermission")
  fun sendMessageToClient(message: String) {
    val device = connectedDevice ?: return
    val characteristic = gattService.getCharacteristic(BluechatConstants.MESSAGE_UUID)

    characteristic.value = message.toByteArray(Charsets.UTF_8)
    gattServer?.notifyCharacteristicChanged(device, characteristic, false)
  }

  @SuppressLint("MissingPermission")
  fun disconnectClient() {
    connectedDevice?.let { device ->
      /* When Server tries to disconnect, it doesn't send a callback to client about disconnection
      * We do a "workaround" of sending a specific message
      * and inside ClientManager, if the message string matches -> we disconnect client too. */
      sendMessageToClient(BluechatConstants.DISCONNECT_MSG)
      Handler(Looper.getMainLooper()).postDelayed({
        gattServer?.cancelConnection(device)
      }, 200)
      Log.d(BluechatConstants.LOG_TAG, "Server disconnected client ${device.address}")
      connectedDevice = null
      emitter.connectionChangeEmitter(BluetoothProfile.STATE_DISCONNECTED, "none", null)
    }
  }

  @SuppressLint("MissingPermission")
  private fun startAdvertising() {
    val aSettings =
      AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
        .setConnectable(true)
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
        .setTimeout(0)
        .build()

    val aData = AdvertiseData.Builder()
      .setIncludeDeviceName(true)
      .addServiceUuid(ParcelUuid(BluechatConstants.SERVICE_UUID))
      .build()

    advertiser.let { advertiser ->
      advertiser?.startAdvertising(
        aSettings,
        aData,
        advertiseCallback
      )
    }
  }

  @SuppressLint("MissingPermission")
  private fun stopAdvertising() {
    advertiser.let { advertiser ->
      advertiser?.stopAdvertising(advertiseCallback)
    }
  }

  private val advertiseCallback = object : AdvertiseCallback() {
    override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
      advertisePromise?.resolve(0)
      advertisePromise = null
    }

    override fun onStartFailure(errorCode: Int) {
      Log.d(BluechatConstants.LOG_TAG, "Failed to start Advertising: $errorCode")
      advertisePromise?.resolve(errorCode)
      advertisePromise = null
    }
  }

  @SuppressLint("MissingPermission")
  private var serverCallback = object : BluetoothGattServerCallback() {
    override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
      super.onConnectionStateChange(device, status, newState)

      if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
        connectedDevice = device
        emitter.connectionChangeEmitter(newState, "server", device?.name ?: device?.address)
      } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
        connectedDevice = null
        Log.d(BluechatConstants.LOG_TAG, "Device disconnected")
        emitter.connectionChangeEmitter(newState, "none", null)
      }
    }

    override fun onCharacteristicWriteRequest(
      device: BluetoothDevice?,
      requestId: Int,
      characteristic: BluetoothGattCharacteristic?,
      preparedWrite: Boolean,
      responseNeeded: Boolean,
      offset: Int,
      value: ByteArray?
    ) {
      super.onCharacteristicWriteRequest(
        device,
        requestId,
        characteristic,
        preparedWrite,
        responseNeeded,
        offset,
        value
      )
      val message = value?.toString(Charsets.UTF_8)
      Log.d(BluechatConstants.LOG_TAG, "Message received: $message")

      message?.let { emitter.onMessageSendEmitter(it) }

      if (responseNeeded) {
        gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null)
      }
    }

    override fun onDescriptorWriteRequest(
      device: BluetoothDevice?,
      requestId: Int,
      descriptor: BluetoothGattDescriptor?,
      preparedWrite: Boolean,
      responseNeeded: Boolean,
      offset: Int,
      value: ByteArray?
    ) {
      super.onDescriptorWriteRequest(
        device,
        requestId,
        descriptor,
        preparedWrite,
        responseNeeded,
        offset,
        value
      )
      Log.d(BluechatConstants.LOG_TAG, "Descriptor write request received")

      if (responseNeeded) {
        gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null)
      }
    }
  }
}
