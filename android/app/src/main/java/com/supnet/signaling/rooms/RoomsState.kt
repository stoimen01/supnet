package com.supnet.signaling.rooms

import com.supnet.signaling.entities.Message
import com.supnet.signaling.entities.Room

sealed class RoomsState {

    object Idle: RoomsState()

    object Connecting : RoomsState()

    object Connected : RoomsState()

    object Disconnecting : RoomsState()

    object Disconnected : RoomsState()

    data class InLobby(val rooms: List<Room>) : RoomsState()

    data class CreatingRoom(val rooms: List<Room>) : RoomsState()

    data class JoiningRoom(
        val room: Room,
        val rooms: List<Room>
    ) : RoomsState()

    data class InRoom(
        val room: Room,
        val rooms: List<Room>,
        val messages: Set<Message>
    ) : RoomsState()

}