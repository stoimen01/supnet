package sessions

import io.ktor.sessions.CurrentSession
import io.ktor.sessions.get
import io.ktor.sessions.set
import io.ktor.util.nextNonce

class SessionsManager {

    fun onSession(sessions: CurrentSession) {
        if (sessions.get<SignalingSession>() == null) {
            sessions.set(SignalingSession(nextNonce()))
        }
    }

    fun isSessionValid(sessions: CurrentSession) =
            sessions.get<SignalingSession>() != null

    fun getSession(sessions: CurrentSession): SignalingSession? {
        return sessions.get<SignalingSession>()
    }

}