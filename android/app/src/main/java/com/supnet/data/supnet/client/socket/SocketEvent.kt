package com.supnet.data.supnet.client.socket

import com.supnet.data.supnet.SocketState
import okio.ByteString

sealed class SocketEvent {

    data class MessageEvent(val data: ByteString) : SocketEvent()

    data class StateEvent(val state: SocketState) : SocketEvent()

}