import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.websocket.WebSockets
import store.db.DatabaseStore
import java.time.Duration

fun Application.main() {

    val tokensContainer = TokensContainer()
    val store = DatabaseStore()
    val wsManager = SignallingService(tokensContainer, store)
    val usersManager = UsersService(tokensContainer, store, wsManager)
    val gatekeeper = Gatekeeper(wsManager, usersManager)

    install(DefaultHeaders)

    install(CallLogging)

    install(StatusPages) {
        exception<Throwable> { call.respond(HttpStatusCode.InternalServerError) }
    }

    install(ContentNegotiation) {
        jackson { configure(SerializationFeature.INDENT_OUTPUT, true) }
    }

    install(WebSockets) { pingPeriod = Duration.ofMinutes(1) }

    routing { gatekeeper.openGates(this) }
}


