package signaling

import com.google.protobuf.InvalidProtocolBufferException
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.channels.*
import proto.*
import proto.SignalingIntent.IntentCase.*
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.*

class SignalingManager {

    data class User(val id: String, val name: String)

    data class Room(val id: UUID, val name: String, val users: List<User>)

    private val usersCounter = AtomicInteger()

    private val memberNames = ConcurrentHashMap<String, String>()

    private val members = ConcurrentHashMap<String, MutableList<WebSocketSession>>()

    private val rooms = ConcurrentHashMap<UUID, Room>()

    private val lastMessages = LinkedList<String>()

    /**
     * Handles that a [member] with a specific [socket] left the server.
     */
    private suspend fun memberLeft(member: String, socket: WebSocketSession) {
        // Removes the socket connection for this member
        val connections = members[member]
        connections?.remove(socket)

        // If no more sockets are connected for this member, let's remove it from the server
        // and notify the rest of the users about this event.
        if (connections != null && connections.isEmpty()) {
            val name = memberNames.remove(member) ?: member
            broadcast("server", "Member left: $name.")
        }
    }

    /**
     * Handles sending to a [recipient] from a [sender] a [message].
     *
     * Both [recipient] and [sender] are identified by its session-id.
     */
    private suspend fun sendTo(recipient: String, sender: String, message: String) {
        members[recipient]?.send(Frame.Text("[$sender] $message"))
    }

    /**
     * Handles a [message] sent from a [sender] by notifying the rest of the users.
     */
    private suspend fun message(sender: String, message: String) {
        // Pre-format the message to be send, to prevent doing it for all the users or connected sockets.
        val name = memberNames[sender] ?: sender
        val formatted = "[$name] $message"

        // Sends this pre-formatted message to all the members in the server.
        broadcast(formatted)

        // Appends the message to the list of [lastMessages] and caps that collection to 100 items to prevent
        // growing too much.
        synchronized(lastMessages) {
            lastMessages.add(formatted)
            if (lastMessages.size > 100) {
                lastMessages.removeFirst()
            }
        }
    }

    private suspend fun broadcastBinary(buffer: ByteBuffer) {
        members.values.forEach { socket ->
            socket.send(Frame.Binary(true, buffer))
        }
    }

    /**
     * Sends a [message] to all the members in the server, including all the connections per member.
     */
    private suspend fun broadcast(message: String) {
        members.values.forEach { socket ->
            socket.send(Frame.Text(message))
        }
    }

    /**
     * Sends a [message] coming from a [sender] to all the members in the server, including all the connections per member.
     */
    private suspend fun broadcast(sender: String, message: String) {
        val name = memberNames[sender] ?: sender
        broadcast("[$name] $message")
    }

    /**
     * Sends a [message] to a list of [this] [WebSocketSession].
     */
    private suspend fun List<WebSocketSession>.send(frame: Frame) {
        forEach {
            try {
                it.send(frame.copy())
            } catch (t: Throwable) {
                try {
                    it.close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, ""))
                } catch (ignore: ClosedSendChannelException) {
                    // at some point it will get closed
                }
            }
        }
    }

    suspend fun onNewSession(id: String, wsSession: DefaultWebSocketServerSession) {

        val name = memberNames.computeIfAbsent(id) { "user${usersCounter.incrementAndGet()}" }

        val list = members.computeIfAbsent(id) { CopyOnWriteArrayList<WebSocketSession>() }
        list.add(wsSession)

        // sending current rooms info
        val infoEvent = rooms.values.toRoomsInfo()
        wsSession.send(Frame.Binary(true, ByteBuffer.wrap(infoEvent.toByteArray())))

        try {
            wsSession.incoming.consumeEach { frame ->
                // accepting only binary frames
                if (frame !is Frame.Binary) return@consumeEach
                try {
                    val intent = SignalingIntent.parseFrom(frame.readBytes())
                    when (intent.intentCase) {
                        CREATE_ROOM -> onCreateRoom(User(id, name), intent.createRoom)
                        JOIN_ROOM -> onJoinRoom(intent.joinRoom)
                        LEAVE_ROOM -> onLeaveRoom(intent.leaveRoom)
                        INTENT_NOT_SET, null -> { /* no-op */ }
                    }
                } catch (ex : InvalidProtocolBufferException) {
                    wsSession.close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "INVALID INTENT"))
                }
            }
        } finally {
            memberLeft(id, wsSession)
        }
    }

    private suspend fun onCreateRoom(creator: User, intent: CreateRoomIntent) {

        // check if room with such name already exists
        val room = rooms.values.find { it.name == intent.name }
        if (room == null) {

            val newRoom = rooms.computeIfAbsent(UUID.randomUUID()) {
                Room(it, intent.name, listOf(creator))
            }

            val event = RoomCreatedEvent.newBuilder()
                    .setId(newRoom.id.toString())
                    .setName(newRoom.name)
                    .build()

            broadcastBinary(ByteBuffer.wrap(event.toByteArray()))

        } else {


        }
    }


    private fun onLeaveRoom(intent: LeaveRoomIntent) {


    }

    private fun onJoinRoom(intent: JoinRoomIntent) {

    }



    private fun MutableCollection<Room>.toRoomsInfo(): RoomsInfoEvent {
        val rooms = map { (id, name, users) ->

            val roomMembers = users.map {
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
