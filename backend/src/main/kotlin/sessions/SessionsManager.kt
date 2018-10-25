package sessions

import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.close
import io.ktor.sessions.CurrentSession
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import io.ktor.util.nextNonce
import io.ktor.websocket.DefaultWebSocketServerSession
import signaling.SignalingManager

class SessionsManager(
        private val signalingManager: SignalingManager
) {

    fun onSession(sessions: CurrentSession) {
        if (sessions.get<SignalingSession>() == null) {
            println("SESSION CREATED")
            sessions.set(SignalingSession(nextNonce()))
        }
    }

    suspend fun onWsSession(wsSession: DefaultWebSocketServerSession) {

        val session = wsSession.call.sessions.get<SignalingSession>()
        if (session == null) {
            println("WS SESSION CLOSED")
            wsSession.close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Session is invalid or unavailable"))
        } else {
            println("WS SESSION PASSED")
            signalingManager.onNewSession(session.id, wsSession)
        }

    }

}