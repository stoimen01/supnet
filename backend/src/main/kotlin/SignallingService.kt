import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException
import proto.InvitationAcceptedEvent
import proto.InvitationEvent
import proto.WsEvent
import store.SupnetStore
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class SignallingService(
        private val tokensContainer: TokensContainer,
        private val store: SupnetStore
) {

    private val connections = ConcurrentHashMap<UserID, MutableList<WebSocketSession>>()

    suspend fun onConnection(token: UUID, wsSession: DefaultWebSocketServerSession) {

        val userId = tokensContainer.get(token)
        if (userId == null) {
            wsSession.close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Provided token is invalid !"))
            return
        }

        connections.computeIfAbsent(userId) { CopyOnWriteArrayList<WebSocketSession>() }.add(wsSession)

        store.getInvitationsByRecipient(userId).forEach { tryToSendInvitation(it) }

        try {
            while (true) {
                val frame = wsSession.incoming.receive() as? Frame.Binary ?: continue

            }
        } catch (e: ClosedReceiveChannelException) {
            e.printStackTrace()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    suspend fun notifyInvitationAccepted(initiator: UserID, invId: InvitationID, friendId: UserID, friendName: String) {
        if (!connections.containsKey(initiator)) return

        val event = InvitationAcceptedEvent.newBuilder()
                .setInvitationId(invId)
                .setFriendId(friendId)
                .setFriendName(friendName)

        sendEvent(initiator) { setInvitationAccepted(event) }
    }

    suspend fun tryToSendInvitation(invitation: Invitation) {
        if (!connections.containsKey(invitation.recipientId)) return
        val (invId, initiatorId, recipientId, msg) = invitation
        val initiator = store.getUserById(initiatorId) ?: return

        val event = InvitationEvent.newBuilder()
                .setInvitationId(invId)
                .setSenderName(initiator.name)
                .setMessage(msg)

        sendEvent(recipientId) { setInvitation(event) }
    }

    private fun buildEvent(builder: WsEvent.Builder.() -> Unit): WsEvent {
        return WsEvent.newBuilder()
                .apply { builder() }
                .build()
    }

    /* Sends event to all connections for particular user */
    private suspend fun sendEvent(id: UserID, builder: WsEvent.Builder.() -> Unit) {
        val event = buildEvent(builder)
        connections[id]?.send(event)
    }

    private suspend fun sendEvent(id: UserID, event: WsEvent) {
        connections[id]?.send(event)
    }

    private suspend fun List<WebSocketSession>.send(event: WsEvent) = forEach { it.send(event) }

    private suspend fun WebSocketSession.send(event: WsEvent) {
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
}