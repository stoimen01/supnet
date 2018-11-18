package com.supnet.signaling.rooms

import java.util.*

sealed class RoomsEffect {

    sealed class SignalingEffect : RoomsEffect() {
        object Connect: SignalingEffect()
        object Disconnect: SignalingEffect()
        data class CreateRoom(val name: String): SignalingEffect()
        data class JoinRoom(val roomId: UUID): SignalingEffect()
        data class LeaveRoom(val roomId: UUID): SignalingEffect()
        data class SendMessage(val data: String) : SignalingEffect()
    }

}