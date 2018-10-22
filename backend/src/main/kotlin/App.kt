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

    val signalingManager = SignalingManager()
    val sessionManager = SessionsManager(signalingManager)

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
        webSocket("/signaling", sessionManager::onNewSession)
    }
}