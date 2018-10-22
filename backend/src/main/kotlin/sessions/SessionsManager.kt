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
            sessions.set(SignalingSession(nextNonce()))
        }
    }

    suspend fun onNewSession(wsSession: DefaultWebSocketServerSession) {

        val session = wsSession.call.sessions.get<SignalingSession>()
        if (session == null) {
            wsSession.close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Session is invalid or unavailable"))
        } else {
            signalingManager.onNewSession(session.id, wsSession)
        }

    }

}