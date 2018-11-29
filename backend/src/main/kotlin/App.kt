import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.close
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import sessions.*
import signaling.SignalingManager
import java.time.Duration

fun Application.main() {

    val usersService = UsersService()
    val signalingManager = SignalingManager()
    val sessionManager = SessionsManager(signalingManager)

    install(DefaultHeaders)
    install(CallLogging)

    install(Sessions) {
        cookie<SignalingSession>("SESSION")
    }
    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }
    }

    install(WebSockets) {
        pingPeriod = Duration.ofMinutes(1)
    }

    intercept(ApplicationCallPipeline.Features) {
        sessionManager.onSession(call.sessions)
    }

    DatabaseFactory.init()

    routing {

        post("/register") {
            val user = call.receive<NewUser>()
            usersService.addUser(user)
            call.respond(HttpStatusCode.Created)
            //call.respond(HttpStatusCode.Conflict)
        }

        webSocket("/signaling") {
            sessionManager.onWsSession(this)
        }
    }
}