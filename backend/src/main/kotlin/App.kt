import io.ktor.application.*
import io.ktor.features.DefaultHeaders
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import sessions.*
import signaling.SignalingManager
import java.time.Duration

fun Application.main() {
    ChatApplication().apply { main() }
}

class ChatApplication {

    private val sessionManager = SessionsManager()
    private val signalingManager = SignalingManager(sessionManager)

    fun Application.main() {

        install(DefaultHeaders)

        install(Sessions) {
            cookie<SignalingSession>("SESSION")
        }

        install(WebSockets) {
            pingPeriod = Duration.ofMinutes(1)
        }

        intercept(ApplicationCallPipeline.Features) {
            sessionManager.onSession(call.sessions)
        }

        routing {
            webSocket("/signaling") {
                signalingManager.onNewSession(this)
            }
        }
    }
}