package com.bluechat

interface BluechatEventEmitters {
  fun connectionChangeEmitter(state: Int, role: String, connectedDevice: String?)
  fun onMessageSendEmitter(value: String)
}
