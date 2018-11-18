package com.supnet.signaling.rooms

import com.supnet.signaling.entities.Room
import java.util.*

sealed class RoomsEvent {

    sealed class UIEvent : RoomsEvent() {
        object OnConnect : UIEvent()
        object OnDisconnect : UIEvent()
        data class OnCreateRoom(val name: String) : UIEvent()
        data class OnJoinRoom(val roomId: UUID) : UIEvent()
        data class OnSendMessage(val data: String) : UIEvent()
        object OnLeaveRoom : UIEvent()
    }

    sealed class SignalingEvent : RoomsEvent() {

        object SocketConnected: SignalingEvent()
        object SocketDisconnected: SignalingEvent()
        object SocketFailed: SignalingEvent()

        data class RoomsReceived(val rooms: List<Room>) : SignalingEvent()
        data class RoomCreated(val room: Room) : SignalingEvent()
        object RoomNotCreated : SignalingEvent()

        data class RoomRemoved(val roomId: UUID) : SignalingEvent()

        object RoomJoined : SignalingEvent()
        object RoomNotJoined : SignalingEvent()

        object RoomLeaved : SignalingEvent()
        object RoomNotLeaved : SignalingEvent()

        object MessageSend : SignalingEvent()
        object MessageNotSend : SignalingEvent()
        data class MessageReceived(val sender: String, val data: String) : SignalingEvent()
    }
}