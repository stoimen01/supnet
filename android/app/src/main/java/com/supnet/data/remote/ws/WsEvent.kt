package com.supnet.data.remote.ws

sealed class WsEvent {

    data class InvitationAccepted(
        val invitationId: Int,
        val friendId: Int,
        val friendName: String
    ) : WsEvent()

    data class InvitationReceived(
        val invitationId: Int,
        val senderName: String,
        val message: String
    ) : WsEvent()

}