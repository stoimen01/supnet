import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.auth.authentication
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.header
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import signaling.SignalingManager
import java.time.Duration

fun Application.main() {

    val signalingManager = SignalingManager()
    val usersManager = UsersManager(signalingManager)

    initDatabase()

    install(DefaultHeaders)
    install(CallLogging)

    install(StatusPages) {
        exception<Throwable> { cause -> call.respond(HttpStatusCode.InternalServerError) }
    }

    install(ContentNegotiation) {
        jackson { configure(SerializationFeature.INDENT_OUTPUT, true) }
    }

    install(WebSockets) { pingPeriod = Duration.ofMinutes(1) }

    routing {

        authenticate()

        post("/register") {
            val user = call.receive<NewUser>()
            val token = usersManager.addUser(user)
            if (token == null) {
                call.respond(HttpStatusCode.Conflict)
            } else {
                call.respond(HttpStatusCode.Created, token)
            }
        }

        post("/login") {
            val credentials = call.receive<LoginCredentials>()
            val token = usersManager.loginUser(credentials)
            if (token == null) {
                call.respond(HttpStatusCode.BadRequest)
            } else {
                call.respond(HttpStatusCode.OK, token)
            }
        }

        webSocket("/signaling") {
            usersManager.onWsSession(this)
        }
    }
}

fun Route.authenticate() {
    val bearer = "Bearer "
    intercept(ApplicationCallPipeline.Features) {
        val authorization = call.request.header(HttpHeaders.Authorization) ?: return@intercept
        if (!authorization.startsWith(bearer)) return@intercept
        val token = authorization.removePrefix(bearer).trim()
        call.authentication.principal(SupnetPrincipal(token))
    }
}

