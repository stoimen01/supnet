package com.supnet.data.remote.ws

import okhttp3.WebSocket

sealed class WsEvent {

    sealed class WsMessageEvent : WsEvent() {

        data class InvitationAccepted(
            val invitationId: Int,
            val friendId: Int,
            val friendName: String
        ) : WsMessageEvent()

        data class InvitationReceived(
            val invitationId: Int,
            val senderName: String,
            val message: String
        ) : WsMessageEvent()
    }

    sealed class WsStateEvent : WsEvent() {

        data class WsOpen(val ws: WebSocket) : WsStateEvent()

        object WsClosed : WsStateEvent()

    }
}