package com.supnet.signaling.rooms

import com.supnet.signaling.client.SignalingClient
import com.supnet.signaling.client.SignalingClient.SignalingIntent.*
import com.supnet.signaling.entities.Message
import com.supnet.signaling.entities.MessageStatus
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
import java.lang.IllegalStateException
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

    override fun leaveRoom() = uiEvents.onNext(OnLeaveRoom)

    override fun getState() = stateStream

    override fun getRoomData(): Observable<Pair<Room, Set<Message>>> {
        return stateStream
            .flatMap { state ->
                return@flatMap when(state) {
                    is InRoom -> Observable.just(Pair(state.room, state.messages))
                    else -> Observable.error(IllegalStateException("Cannot provide room when not in room."))
                }
            }
    }

    override fun getRooms(): Observable<List<Room>> {
        return stateStream
            .flatMap { state ->
                return@flatMap when(state) {
                    is InLobby -> Observable.just(state.rooms)
                    else -> Observable.error(IllegalStateException("Cannot provide rooms when not in lobby."))
                }
            }
    }

    override fun getStateLog(): Observable<String> = stateLog

    override fun sendMessage(msg: String) = uiEvents.onNext(OnSendMessage(msg))

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
                CreatingRoom(lastState.rooms)
            }
            is OnJoinRoom -> {
                signalingClient.processIntent(JoinRoom(event.roomId))
                val room = lastState.rooms.first { event.roomId == it.id }
                JoiningRoom(room, lastState.rooms)
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
                InRoom(event.room, lastState.rooms + event.room, setOf())
            }
            is RoomRemoved -> {
                // rooms are kept in the back state and we need to update it
                val rooms = lastState.rooms.filter { it.id != event.roomId }
                lastState.copy(rooms)
            }
            RoomNotCreated -> {
                // TODO: emit error event
                InLobby(lastState.rooms)
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
                lastState.copy(rooms = lastState.rooms + event.room)
            }
            is RoomRemoved -> {
                lastState.copy(rooms = lastState.rooms.filter { it.id != event.roomId })
            }
            RoomJoined -> InRoom(lastState.room, lastState.rooms, setOf())
            RoomNotJoined -> {
                // TODO: emit error event
                InLobby(lastState.rooms)
            }
            else -> throw IllegalEventException(lastState, event)
        }

        is InRoom -> when (event) {
            OnDisconnect -> {
                signalingClient.processIntent(Disconnect)
                Disconnecting
            }
            OnLeaveRoom -> {
                signalingClient.processIntent(LeaveRoom(lastState.room.id))
                InLobby(lastState.rooms)
            }
            SocketDisconnected, SocketFailed -> Disconnected
            is RoomCreated -> {
                lastState.copy(rooms = lastState.rooms + event.room)
            }
            is RoomRemoved -> {
                lastState.copy(rooms = lastState.rooms.filter { it.id != event.roomId })
            }
            is RoomJoined -> {
                lastState // TODO notify
            }
            is OnSendMessage -> {
                signalingClient.processIntent(SendMessage(event.data))
                lastState.copy(messages = lastState.messages + Message("You", event.data, MessageStatus.SENDING))
            }
            is MessageReceived -> {
               lastState.copy(messages = lastState.messages + Message(event.sender, event.data, MessageStatus.SENT))
            }
            MessageSend -> {
                val messages = lastState.messages.toMutableSet()
                val origMsg = messages.first { it.status == MessageStatus.SENDING }
                val newMsg = origMsg.copy(status = MessageStatus.SENT)
                messages.remove(origMsg)
                messages.add(newMsg)
                lastState.copy(messages = messages)
            }
            MessageNotSend -> {
                val messages = lastState.messages.toMutableSet()
                val origMsg = messages.first { it.status == MessageStatus.SENDING }
                val newMsg = origMsg.copy(status = MessageStatus.FAILED)
                messages.remove(origMsg)
                messages.add(newMsg)
                lastState.copy(messages = messages)
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