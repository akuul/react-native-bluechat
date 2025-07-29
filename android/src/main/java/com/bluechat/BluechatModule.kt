package com.bluechat

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.IntentFilter
import android.os.ParcelUuid
import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = BluechatModule.NAME)
class BluechatModule(reactContext: ReactApplicationContext) :
  NativeBluechatSpec(reactContext), BluechatEventEmitters {
  private val bManager = reactApplicationContext.getSystemService(BluetoothManager::class.java)
  private val bAdapter = bManager.adapter

  val serverManager = BluechatServerManager(reactApplicationContext, bManager, this)
  val clientManager = BluechatClientManager(reactApplicationContext, bAdapter, this)

  private var bluetoothStateReceiver: BluetoothStateReceiver = BluetoothStateReceiver { eventData ->
    emitOnBluetoothStateChanged(eventData)
  }

  private var leScanCallback: ScanCallback? = null

  init {
    val bluetoothStateIntent = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
    reactApplicationContext.registerReceiver(bluetoothStateReceiver, bluetoothStateIntent)
  }

  override fun isEnabled(): Boolean {
    return bAdapter.isEnabled
  }

  @SuppressLint("MissingPermission")
  override fun startScan() {
    if (!isBluetoothReady()) {
      return
    }

    Log.d(BluechatConstants.LOG_TAG, "INITIALIZED SCANNING")

    val leScanSettings =
      ScanSettings.Builder().setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).setScanMode(
        ScanSettings.SCAN_MODE_BALANCED
      ).build()

    val leScanFilter = listOf(
      ScanFilter.Builder()
        .setServiceUuid(ParcelUuid(BluechatConstants.SERVICE_UUID))
        .build()
    )

    leScanCallback = object : ScanCallback() {
      override fun onScanResult(callbackType: Int, result: ScanResult?) {
        super.onScanResult(callbackType, result)
        val otherDevice = result?.device
        val deviceName = otherDevice?.name

        if (deviceName.isNullOrBlank()) return

        val eventData = Arguments.createMap().apply {
          putString("name", deviceName)
          putString("address", otherDevice.address)
        }

        emitOnBluetoothScan(eventData)
      }
    }

    bAdapter.bluetoothLeScanner.startScan(
      leScanFilter,
      leScanSettings,
      leScanCallback
    )
  }

  @SuppressLint("MissingPermission")
  override fun stopScan() {
    leScanCallback?.let {
      bAdapter.bluetoothLeScanner.stopScan(it)
      Log.d(BluechatConstants.LOG_TAG, "STOPPED SCANNING")
      leScanCallback = null
    }
  }

  override fun startAdvertise(promise: Promise?) {
    serverManager.startServer(promise)
  }

  override fun stopAdvertise() {
    serverManager.stopServer()
  }

  @SuppressLint("MissingPermission")
  override fun connectToDevice(address: String?) {
    if (address == null || !BluetoothAdapter.checkBluetoothAddress(address)) return
    clientManager.connectToDevice(address)
  }

  override fun disconnectFromDevice() {
    clientManager.disconnect()
    serverManager.disconnectClient()
  }

  override fun connectionChangeEmitter(state: Int, role: String, connectedDevice: String?) {
    val eventData = Arguments.createMap().apply {
      putInt("state", state)
      putString("role", role)
      putString("connectedDevice", connectedDevice)
    }

    emitOnConnectionChanged(eventData)
  }

  override fun onMessageSendEmitter(value: String) {
    val eventData = Arguments.createMap().apply {
      putString("value", value)
    }

    emitOnMessageReceived(eventData)
  }

  override fun sendMessageToClient(message: String?) {
    message?.let { serverManager.sendMessageToClient(it) }
  }

  override fun sendMessageToServer(message: String?) {
    message?.let { clientManager.sendMessageToServer(it) }
  }

  fun isBluetoothReady(): Boolean {
    return bAdapter != null && bAdapter.isEnabled
  }

  override fun getName(): String {
    return NAME
  }

  override fun invalidate() {
    super.invalidate()
    reactApplicationContext.unregisterReceiver(bluetoothStateReceiver)
  }

  companion object {
    const val NAME = "Bluechat"
  }
}
