package signaling

import io.ktor.http.cio.websocket.*
import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.channels.*
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.*

class SignalingManager {

    private val usersCounter = AtomicInteger()

    private val memberNames = ConcurrentHashMap<String, String>()

    private val members = ConcurrentHashMap<String, MutableList<WebSocketSession>>()

    private val lastMessages = LinkedList<String>()

    /**
     * Handles a [member] idenitified by its session id renaming [to] a specific name.
     */
    private suspend fun memberRenamed(member: String, to: String) {
        // Re-sets the member name.
        val oldName = memberNames.put(member, to) ?: member
        // Notifies everyone in the server about this change.
        broadcast("server", "Member renamed from $oldName to $to")
    }

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
     * Handles the 'who' command by sending the member a list of all all members names in the server.
     */
    private suspend fun who(sender: String) {
        members[sender]?.send(Frame.Text(memberNames.values.joinToString(prefix = "[server::who] ")))
    }

    /**
     * Handles the 'help' command by sending the member a list of available commands.
     */
    private suspend fun help(sender: String) {
        members[sender]?.send(Frame.Text("[server::help] Possible commands are: /user, /help and /who"))
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

        if (list.size == 1) {
            broadcast("server", "Member joined: $name.")
        }

        val messages = synchronized(lastMessages) { lastMessages.toList() }
        for (message in messages) {
            wsSession.send(Frame.Text(message))
        }

        try {
            wsSession.incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    receivedMessage(id, frame.readText())
                }
            }
        } finally {
            memberLeft(id, wsSession)
        }

    }


    private suspend fun receivedMessage(id: String, command: String) {
        // We are going to handle commands (text starting with '/') and normal messages
        when {
            // The command `who` responds the user about all the member names connected to the user.
            command.startsWith("/who") -> who(id)
            // The command `user` allows the user to set its name.
            command.startsWith("/user") -> {
                // We strip the command part to get the rest of the parameters.
                // In this case the only parameter is the user's newName.
                val newName = command.removePrefix("/user").trim()
                // We verify that it is a valid name (in terms of length) to prevent abusing
                when {
                    newName.isEmpty() -> sendTo(id, "signalingManager::help", "/user [newName]")
                    newName.length > 50 -> sendTo(
                            id,
                            "signalingManager::help",
                            "new name is too long: 50 characters limit"
                    )
                    else -> memberRenamed(id, newName)
                }
            }
            // The command 'help' allows users to get a list of available commands.
            command.startsWith("/help") -> help(id)
            // If no commands matched at this point, we notify about it.
            command.startsWith("/") -> sendTo(
                    id,
                    "signalingManager::help",
                    "Unknown command ${command.takeWhile { !it.isWhitespace() }}"
            )
            // Handle a normal message.
            else -> message(id, command)
        }
    }

}
