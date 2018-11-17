package com.supnet.signaling.rooms

import com.supnet.signaling.entities.Message
import com.supnet.signaling.entities.Room
import io.reactivex.Observable
import java.util.*

interface RoomsManager {

    sealed class State {

        object Idle: State()

        object Connecting : State()

        object Connected : State()

        object Disconnecting : State()

        object Disconnected : State()

        data class InLobby(val rooms: List<Room>) : State()

        data class CreatingRoom(val rooms: List<Room>) : State()

        data class JoiningRoom(
            val room: Room,
            val rooms: List<Room>
        ) : State()

        data class InRoom(
            val room: Room,
            val rooms: List<Room>,
            val messages: Set<Message>
        ) : State()

    }

    sealed class Event {

        sealed class UIEvent : Event() {
            object OnConnect : UIEvent()
            object OnDisconnect : UIEvent()
            data class OnCreateRoom(val name: String) : UIEvent()
            data class OnJoinRoom(val roomId: UUID) : UIEvent()
            data class OnSendMessage(val data: String) : UIEvent()
            object OnLeaveRoom : UIEvent()
        }

        sealed class ConnectionEvent : Event() {
            object SocketConnected: ConnectionEvent()
            object SocketDisconnected: ConnectionEvent()
            object SocketFailed: ConnectionEvent()
            data class RoomsReceived(val rooms: List<Room>) : ConnectionEvent()
            data class RoomCreated(val room: Room) : ConnectionEvent()
            data class RoomRemoved(val roomId: UUID) : ConnectionEvent()
            object RoomNotCreated : ConnectionEvent()
            object RoomJoined : ConnectionEvent()
            object RoomNotJoined : ConnectionEvent()
            object MessageSend : ConnectionEvent()
            object MessageNotSend : ConnectionEvent()
            data class MessageReceived(val sender: String, val data: String) : ConnectionEvent()
        }
    }

    class IllegalEventException(
        state: RoomsManager.State,
        event: RoomsManager.Event
    ) : Exception("Illegal event: $event happened on state: $state")

    fun connect()
    fun disconnect()
    fun createRoom(name: String)
    fun joinRoom(roomId: UUID)
    fun leaveRoom()
    fun getState(): Observable<State>
    fun getStateLog(): Observable<String>
    fun getRoomData(): Observable<Pair<Room, Set<Message>>>
    fun getRooms(): Observable<List<Room>>
    fun sendMessage(msg: String)
}