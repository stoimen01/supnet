package com.supnet.signaling.rooms

import com.supnet.signaling.entities.Message
import com.supnet.signaling.entities.MessageStatus
import com.supnet.signaling.rooms.RoomsEffect.SignalingEffect.*
import com.supnet.signaling.rooms.RoomsEvent.SignalingEvent.*
import com.supnet.signaling.rooms.RoomsEvent.UIEvent.*
import com.supnet.signaling.rooms.RoomsState.*
import com.supnet.signaling.rooms.StateReducer.IllegalEventException

private typealias RoomsReduceResult = ReduceResult<RoomsState, RoomsEffect>

class RoomsReducer : StateReducer<RoomsState, RoomsEvent, RoomsEffect> {

    override fun reduce(
        lastResult: RoomsReduceResult,
        event: RoomsEvent
    ): RoomsReduceResult = when (val lastState = lastResult.state) {

        Idle -> when (event) {
            OnConnect -> resultOf(Connecting, Connect)
            else -> throwInvalid(lastState, event)
        }

        Connecting -> when (event) {
            SocketConnected -> stateOnly(Connected)
            SocketDisconnected, SocketFailed -> stateOnly(Disconnected)
            else -> throwInvalid(lastState, event)
        }

        Connected -> when (event) {
            OnDisconnect -> resultOf(Disconnecting, Disconnect)
            SocketDisconnected, SocketFailed -> stateOnly(Disconnected)
            is RoomsReceived -> stateOnly(InLobby(event.rooms))
            else -> throwInvalid(lastState, event)
        }

        Disconnecting -> when (event) {
            SocketDisconnected, SocketFailed -> stateOnly(Disconnected)
            else -> throwInvalid(lastState, event)
        }

        is InLobby -> lastState.processEvent(event)

        is CreatingRoom -> lastState.processEvent(event)

        is JoiningRoom -> lastState.processEvent(event)

        is InRoom -> lastState.processEvent(event)

        Disconnected -> when (event) {
            OnConnect -> resultOf(Connecting, Connect)
            else -> throwInvalid(lastState, event)
        }
    }

    private fun InLobby.processEvent(event: RoomsEvent) = when (event) {

        SocketDisconnected, SocketFailed -> stateOnly(Disconnected)

        OnDisconnect -> resultOf(Disconnecting, Disconnect)

        is OnCreateRoom -> resultOf(CreatingRoom(rooms), CreateRoom(event.name))

        is OnJoinRoom -> {
            val room = rooms.first { event.roomId == it.id }
            resultOf(JoiningRoom(room, rooms), JoinRoom(event.roomId))
        }

        is RoomCreated -> stateOnly(copy(rooms = rooms + event.room))

        is RoomRemoved -> stateOnly(copy(rooms = rooms.filter { it.id != event.roomId })) // optimize this

        else -> throwInvalid(this, event)
    }

    private fun CreatingRoom.processEvent(event: RoomsEvent): RoomsReduceResult = when (event) {

        SocketDisconnected, SocketFailed -> stateOnly(Disconnected)

        OnDisconnect -> resultOf(Disconnecting, Disconnect)

        is RoomCreated -> stateOnly(InRoom(event.room, rooms + event.room, setOf()))

        is RoomRemoved -> stateOnly(copy(rooms = rooms.filter { it.id != event.roomId }))

        RoomNotCreated -> stateOnly(InLobby(rooms))

        else -> throwInvalid(this, event)
    }

    private fun JoiningRoom.processEvent(event: RoomsEvent): RoomsReduceResult = when (event) {

        SocketDisconnected, SocketFailed -> stateOnly(Disconnected)

        OnDisconnect -> resultOf(Disconnecting, Disconnect)

        is RoomCreated -> stateOnly(copy(rooms = rooms + event.room))

        is RoomRemoved -> stateOnly(copy(rooms = rooms.filter { it.id != event.roomId }))

        RoomJoined -> stateOnly(InRoom(room, rooms, setOf()))

        RoomNotJoined -> stateOnly(InLobby(rooms))

        else -> throwInvalid(this, event)

    }

    private fun InRoom.processEvent(event: RoomsEvent): RoomsReduceResult = when (event) {

        SocketDisconnected, SocketFailed -> stateOnly(Disconnected)

        OnDisconnect -> resultOf(Disconnecting, Disconnect)

        OnLeaveRoom -> effectOnly(LeaveRoom(room.id))

        is OnSendMessage -> {
            val newState = copy(messages = messages + Message("You", event.data, MessageStatus.SENDING))
            resultOf(newState, SendMessage(event.data))
        }

        is RoomCreated -> stateOnly(copy(rooms = rooms + event.room))

        is RoomRemoved -> stateOnly(copy(rooms = rooms.filter { it.id != event.roomId }))

        is RoomJoined -> repeat()

        RoomLeaved -> stateOnly(InLobby(rooms))

        is MessageReceived -> stateOnly(copy(
            messages = messages + Message(
                event.sender,
                event.data,
                MessageStatus.SENT
            )
        ))

        MessageSend -> {
            val messages = messages.toMutableSet()
            val origMsg = messages.first { it.status == MessageStatus.SENDING }
            val newMsg = origMsg.copy(status = MessageStatus.SENT)
            messages.remove(origMsg)
            messages.add(newMsg)
            stateOnly(copy(messages = messages))
        }

        MessageNotSend -> {
            val messages = messages.toMutableSet()
            val origMsg = messages.first { it.status == MessageStatus.SENDING }
            val newMsg = origMsg.copy(status = MessageStatus.FAILED)
            messages.remove(origMsg)
            messages.add(newMsg)
            stateOnly(copy(messages = messages))
        }

        else -> throwInvalid(this, event)
    }

}