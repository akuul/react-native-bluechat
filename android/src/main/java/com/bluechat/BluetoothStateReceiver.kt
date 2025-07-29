package com.bluechat

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap

class BluetoothStateReceiver(private val onBluetoothStateChanged: (WritableMap) -> Unit) :
  BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {

    val intentAction = intent?.action
    if (intentAction == BluetoothAdapter.ACTION_STATE_CHANGED) {
      val stateValue = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

      val curState = when (stateValue) {
        BluetoothAdapter.STATE_OFF -> "OFF"
        BluetoothAdapter.STATE_TURNING_OFF -> "TURNING_OFF"
        BluetoothAdapter.STATE_ON -> "ON"
        BluetoothAdapter.STATE_TURNING_ON -> "TURNING_ON"
        else -> "unknown"
      }

      val eventData = Arguments.createMap().apply {
        putString("state", curState)
      }
      
      onBluetoothStateChanged(eventData)
    }
  }
}
