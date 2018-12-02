package com.supnet.data.remote.ws

import okhttp3.WebSocket

sealed class WsEvent {

    sealed class WsMessageEvent : WsEvent() {
        object InvitationSending : WsMessageEvent()
        object InvitationSent : WsMessageEvent()
        object InvitationError : WsMessageEvent()
    }

    sealed class WsStateEvent : WsEvent() {

        data class WsOpen(val ws: WebSocket) : WsStateEvent()

        object WsClosed : WsStateEvent()

    }
}