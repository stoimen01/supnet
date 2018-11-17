package signaling

import com.google.protobuf.InvalidProtocolBufferException
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.channels.*
import proto.*
import proto.SignalingIntent.IntentCase.*
import java.lang.IllegalArgumentException
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.*

class SignalingManager {

    data class User(val id: String, val name: String)

    data class Room(val id: UUID, val name: String, val members: MutableSet<User>)

    private val usersCounter = AtomicInteger()

    private val users = ConcurrentHashMap<String, User>()

    private val connections = ConcurrentHashMap<String, MutableList<WebSocketSession>>()

    private val rooms = ConcurrentHashMap<UUID, Room>()

    suspend fun onNewSession(id: String, wsSession: DefaultWebSocketServerSession) {

        val user = users.computeIfAbsent(id) {
            User(id = it, name = "user${usersCounter.incrementAndGet()}")
        }

        val list = connections.computeIfAbsent(id) { CopyOnWriteArrayList<WebSocketSession>() }
        list.add(wsSession)

        // sending current rooms info
        wsSession.send(buildEvent { roomsInfo = rooms.values.toRoomsInfo() })

        try {
            wsSession.incoming.consumeEach { frame ->
                // accepting only binary frames
                if (frame !is Frame.Binary) return@consumeEach
                try {
                    val intent = SignalingIntent.parseFrom(frame.readBytes())
                    when (intent.intentCase) {
                        CREATE_ROOM -> {
                            onCreateRoom(user, intent.createRoom)
                        }
                        JOIN_ROOM -> {
                            onJoinRoom(user, intent.joinRoom)
                        }
                        LEAVE_ROOM -> {
                            onLeaveRoom(user, intent.leaveRoom)
                        }
                        SEND_MESSAGE -> {
                            onSendMessage(user, intent.sendMessage)
                        }
                        INTENT_NOT_SET, null -> { /* no-op */ }
                    }
                } catch (ex : InvalidProtocolBufferException) {
                    wsSession.close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "INVALID INTENT"))
                }
            }
        } finally {
            onDisconnected(user, wsSession)
        }
    }

    private suspend fun onDisconnected(user: User, wsSession: DefaultWebSocketServerSession) {
        val (id) = user

        // Removes the socket connection for this member
        val usrConnections = connections[id]
        usrConnections?.remove(wsSession)

        if (usrConnections != null && usrConnections.isEmpty()) {

            users.remove(id)

            rooms.values.forEach { room ->
                if (room.members.remove(user)) {
                    if (room.members.isEmpty()) {
                        rooms.remove(room.id)
                        broadcastEvent { setRoomRemoved(RoomRemovedEvent.newBuilder().setId(room.id.toString())) }
                    } else {
                        val leaveEvent = buildEvent { setRoomLeaved(RoomLeavedEvent.newBuilder().setId(id)) }
                        room.members.forEach { connections[it.id]?.send(leaveEvent) }
                    }
                }
            }
        }
    }

    private suspend fun onCreateRoom(creator: User, intent: CreateRoomIntent) {

        // room name should not be empty
        if (intent.roomName.isEmpty()) {
            sendEvent(creator.id) { setRoomNotCreated(RoomNotCreatedEvent.newBuilder()) }
            return
        }

        // check if room with such name already exists
        val room = rooms.values.find { it.name == intent.roomName }
        if (room != null) {
            sendEvent(creator.id) { setRoomNotCreated(RoomNotCreatedEvent.newBuilder()) }
            return
        }

        val newRoom = rooms.computeIfAbsent(UUID.randomUUID()) {
            Room(it, intent.roomName, hashSetOf(creator))
        }

        val event = RoomCreatedEvent.newBuilder()
                .setId(newRoom.id.toString())
                .setName(newRoom.name)

        broadcastEvent { setRoomCreated(event) }
    }

    private suspend fun onLeaveRoom(leaver: User, intent: LeaveRoomIntent) {
        try {
            val room = rooms[intent.roomId.toUUID()]
            if (room == null) {
                sendEvent(leaver.id) { setRoomNotLeaved(RoomNotLeavedEvent.newBuilder()) }
                return
            }

            val isRemoved = synchronized(room.members) { room.members.remove(leaver) }
            if (!isRemoved) {
                sendEvent(leaver.id) { setRoomNotLeaved(RoomNotLeavedEvent.newBuilder()) }
                return
            }

            val leaveEvent = buildEvent { setRoomLeaved(RoomLeavedEvent.newBuilder().setId(leaver.id)) }

            if (room.members.isEmpty()) {
                rooms.remove(room.id)
                sendEvent(leaver.id, leaveEvent)
                broadcastEvent { setRoomRemoved(RoomRemovedEvent.newBuilder().setId(room.id.toString())) }
            } else {
                room.members.forEach { connections[it.id]?.send(leaveEvent) }
            }

        } catch (ex: IllegalArgumentException) {
            sendEvent(leaver.id) { setRoomNotLeaved(RoomNotLeavedEvent.newBuilder()) }
        }
    }

    private suspend fun onJoinRoom(user: User, intent: JoinRoomIntent) {
        try {
            val room = rooms[intent.roomId.toUUID()]
            if (room == null) {
                sendEvent(user.id) { setRoomNotJoined(RoomNotJoinedEvent.newBuilder()) }
                return
            }

            val isAdded = synchronized(room.members) { room.members.add(user) }
            if (!isAdded) {
                sendEvent(user.id) { setRoomNotJoined(RoomNotJoinedEvent.newBuilder()) }
                return
            }

            // Notify everyone in the room including the newly joined user that he joined successfully
            val event = buildEvent { setRoomJoined(RoomJoinedEvent.newBuilder().setId(user.id)) }
            room.members.forEach { connections[it.id]?.send(event) }

        } catch (ex: IllegalArgumentException) {
            sendEvent(user.id) { setRoomNotJoined(RoomNotJoinedEvent.newBuilder()) }
        }
    }

    private suspend fun onSendMessage(sender: User, intent: SendMessageIntent) {

        val room = rooms.values.find { it.members.contains(sender) }
        if (room == null) {
            sendEvent(sender.id) { setMessageNotSend(MessageNotSendEvent.newBuilder())}
            return
        }

        // TODO : keep message cache
        val event = MessageReceived.newBuilder()
                .setSenderName(sender.name)
                .setData(intent.data)

        room.members.forEach {
            if (it.id == sender.id) return@forEach
            sendEvent(it.id) { setMessageReceived(event) }
        }

        sendEvent(sender.id) { setMessageSend(MessageSendEvent.newBuilder()) }
    }


    private fun buildEvent(builder: SignalingEvent.Builder.() -> Unit): SignalingEvent {
        return SignalingEvent.newBuilder()
                .apply { builder() }
                .build()
    }

    /* Sends event to all connections for all users */
    private suspend fun broadcastEvent(builder: SignalingEvent.Builder.() -> Unit) {
        val event = buildEvent(builder)
        connections.values.forEach { sockets ->
            sockets.send(event)
        }
    }

    /* Sends event to all connections for particular user */
    private suspend fun sendEvent(id: String, builder: SignalingEvent.Builder.() -> Unit) {
        val event = buildEvent(builder)
        connections[id]?.send(event)
    }

    private suspend fun sendEvent(id: String, event: SignalingEvent) {
        connections[id]?.send(event)
    }

    private suspend fun List<WebSocketSession>.send(event: SignalingEvent) = forEach { it.send(event) }

    private suspend fun WebSocketSession.send(event: SignalingEvent) {
        try {
            send(Frame.Binary(true, ByteBuffer.wrap(event.toByteArray())))
        } catch (t: Throwable) {
            try {
                close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, ""))
            } catch (ignore: ClosedSendChannelException) {
                // at some point it will get closed
            }
        }
    }

    private fun String.toUUID() = UUID.fromString(this)

    private fun MutableCollection<Room>.toRoomsInfo(): RoomsInfoEvent {
        val rooms = map { (id, name, members) ->

            val roomMembers = members.map {
                proto.User.newBuilder()
                        .setId(it.id)
                        .setName(it.name)
                        .build()
            }

            return@map proto.Room.newBuilder()
                    .setId(id.toString())
                    .addAllMembers(roomMembers)
                    .setName(name)
                    .build()
        }

        return RoomsInfoEvent.newBuilder()
                .addAllList(rooms)
                .build()
    }
}
