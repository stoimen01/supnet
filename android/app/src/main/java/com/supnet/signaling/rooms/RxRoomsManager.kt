package com.supnet.signaling.rooms

import com.supnet.signaling.client.SignalingClient
import com.supnet.signaling.client.SignalingClient.SignalingIntent.*
import com.supnet.signaling.entities.Room
import com.supnet.signaling.rooms.RoomsManager.*
import com.supnet.signaling.rooms.RoomsManager.Event.*
import com.supnet.signaling.rooms.RoomsManager.Event.ConnectionEvent.*
import com.supnet.signaling.rooms.RoomsManager.Event.UIEvent.*
import com.supnet.signaling.rooms.RoomsManager.State.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import java.util.*

class RxRoomsManager(
    private val signalingClient: SignalingClient
) : RoomsManager {

    private val uiEvents = PublishSubject.create<UIEvent>()

    private val stateStream = createStateStream()

    private val stateLog = PublishSubject.create<String>()

    override fun connect() = uiEvents.onNext(OnConnect)

    override fun disconnect() = uiEvents.onNext(OnDisconnect)

    override fun createRoom(name: String) = uiEvents.onNext(OnCreateRoom(name))

    override fun joinRoom(roomId: UUID) = uiEvents.onNext(OnJoinRoom(roomId))

    override fun leaveRoom(roomId: UUID) = uiEvents.onNext(OnLeaveRoom(roomId))

    override fun getState() = stateStream

    override fun getStateLog(): Observable<String> = stateLog

    private fun createStateStream(): Observable<RoomsManager.State> {
        return Observable.merge(uiEvents, signalingClient.getEvents())
            .scan<State>(Idle) { lastState, event ->
                stateLog.onNext("Received event: $event on state: $lastState")
                return@scan reduceState(lastState, event)
            }
            .distinctUntilChanged()
            .replay(1)
            .autoConnect()
    }

    private fun reduceState(lastState: State, event: Event) = when (lastState) {

        Idle -> when (event) {
            OnConnect -> {
                signalingClient.processIntent(Connect)
                Connecting
            }
            else -> throw IllegalEventException(lastState, event)
        }

        Connecting -> when (event) {
            SocketConnected -> Connected
            SocketDisconnected, SocketFailed -> Disconnected
            else -> throw IllegalEventException(lastState, event)
        }

        Connected -> when (event) {
            OnDisconnect -> {
                signalingClient.processIntent(Disconnect)
                Disconnecting
            }
            SocketDisconnected, SocketFailed -> Disconnected
            is RoomsReceived -> InLobby(event.rooms)
            else -> throw IllegalEventException(lastState, event)
        }

        Disconnecting -> when (event) {
            SocketDisconnected, SocketFailed -> Disconnected
            else -> throw IllegalEventException(lastState, event)
        }

        is InLobby -> when (event) {
            OnDisconnect -> {
                signalingClient.processIntent(Disconnect)
                Disconnecting
            }
            is OnCreateRoom -> {
                signalingClient.processIntent(CreateRoom(event.name))
                CreatingRoom(lastState)
            }
            is OnJoinRoom -> {
                signalingClient.processIntent(JoinRoom(event.roomId))
                JoiningRoom(lastState, event.roomId)
            }
            SocketDisconnected, SocketFailed -> Disconnected
            is RoomCreated -> {
                lastState.copy(lastState.rooms + event.room)
            }
            is RoomRemoved -> {
                lastState.copy(lastState.rooms.filter { it.id != event.roomId }) // optimize this
            }
            else -> throw IllegalEventException(lastState, event)
        }

        is CreatingRoom -> when (event) {
            OnDisconnect -> {
                signalingClient.processIntent(Disconnect)
                Disconnecting
            }
            SocketDisconnected, SocketFailed -> Disconnected
            is RoomCreated -> {
                InRoom(lastState.backState.copy(rooms = lastState.backState.rooms + event.room), event.room.id)
            }
            is RoomRemoved -> {
                // rooms are kept in the back state and we need to update it
                val rooms = lastState.backState.rooms.filter { it.id != event.roomId }
                val newBackState = lastState.backState.copy(rooms = rooms)
                lastState.copy(newBackState)
            }
            RoomNotCreated -> {
                // TODO: emit error event
                lastState.backState
            }
            else -> throw IllegalEventException(lastState, event)
        }

        is JoiningRoom -> when (event) {
            OnDisconnect -> {
                signalingClient.processIntent(Disconnect)
                Disconnecting
            }
            SocketDisconnected, SocketFailed -> Disconnected
            is RoomCreated -> {
                val rooms = lastState.backState.rooms + event.room
                lastState.copy(lastState.backState.copy(rooms = rooms))
            }
            is RoomRemoved -> {
                val rooms = lastState.backState.rooms.filter { it.id != event.roomId }
                lastState.copy(lastState.backState.copy(rooms = rooms))
            }
            RoomJoined -> InRoom(lastState.backState, lastState.roomId)
            RoomNotJoined -> {
                // TODO: emit error event
                lastState.backState
            }
            else -> throw IllegalEventException(lastState, event)
        }

        is InRoom -> when (event) {
            OnDisconnect -> {
                signalingClient.processIntent(Disconnect)
                Disconnecting
            }
            is OnLeaveRoom -> {
                signalingClient.processIntent(LeaveRoom(event.roomId))
                lastState.backState
            }
            SocketDisconnected, SocketFailed -> Disconnected
            is RoomCreated -> {
                val rooms = lastState.backState.rooms + event.room
                lastState.copy(lastState.backState.copy(rooms = rooms))
            }
            is RoomRemoved -> {
                val rooms = lastState.backState.rooms.filter { it.id != event.roomId }
                lastState.copy(lastState.backState.copy(rooms = rooms))
            }
            else -> throw IllegalEventException(lastState, event)
        }

        Disconnected -> when (event) {
            OnConnect -> {
                signalingClient.processIntent(Connect)
                Connecting
            }
            else -> throw IllegalEventException(lastState, event)
        }
    }

}