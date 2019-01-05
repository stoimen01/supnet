import com.sun.javaws.exceptions.InvalidArgumentException
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.auth.Principal
import io.ktor.auth.authentication
import io.ktor.auth.principal
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.close
import io.ktor.request.ContentTransformationException
import io.ktor.request.header
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.util.pipeline.PipelineContext
import io.ktor.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.webSocket
import java.util.*

class Gatekeeper(
        private val sigService: SignallingService,
        private val usersService: UsersService
) {

    private class SupnetPrincipal(val token: UUID) : Principal

    fun openGates(routing: Routing) = with(routing) {
        authenticate()
        post("/signup") { onSignUp() }
        post("/signoff") { onSignOff() }
        post("/signin") { onSignIn() }
        post("/signout") { onSignOut() }
        post("/invitation/send") { onSendInvitation() }
        post("/invitation/accept") { onAcceptInvitation() }
        post("/invitation/reject") { onRejectInvitation() }
        webSocket("/signalling") { onSignalling() }
    }

    private fun Route.authenticate() {
        val bearer = "Bearer "
        intercept(ApplicationCallPipeline.Features) {
            val authorization = call.request.header(HttpHeaders.Authorization) ?: return@intercept
            if (!authorization.startsWith(bearer)) return@intercept
            val token = authorization.removePrefix(bearer).trim()
            try {
                call.authentication.principal(SupnetPrincipal(UUID.fromString(token)))
            } catch (ex: InvalidArgumentException) {
                return@intercept
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.onSignUp() = guardContent {
        val result = usersService.signUp(call.receive())
        if (result == null) {
            call.respond(HttpStatusCode.Conflict)
        } else {
            call.respond(HttpStatusCode.Created, result)
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.onSignOff() = withToken { token ->
        val isSignedOff = usersService.signOff(token)
        if (!isSignedOff) {
            call.respond(HttpStatusCode.Conflict)
        } else {
            call.respond(HttpStatusCode.OK)
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.onSignIn() = guardContent {
        val result = usersService.signIn(call.receive())
        if (result == null) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            call.respond(HttpStatusCode.OK, result)
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.onSignOut() = withToken { token ->
        val isSignedOff = usersService.signOut(token)
        if (!isSignedOff) {
            call.respond(HttpStatusCode.Conflict)
        } else {
            call.respond(HttpStatusCode.OK)
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.onSendInvitation() = guardContent {
        withToken { token ->
            val isSent = usersService.sendInvitation(call.receive(), token)
            if (!isSent) {
                call.respond(HttpStatusCode.Conflict)
            } else {
                call.respond(HttpStatusCode.OK)
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.onAcceptInvitation() = guardContent {
        withToken { token ->
            val response = usersService.acceptInvitation(call.receive(), token)
            if (response == null) {
                call.respond(HttpStatusCode.Conflict)
            } else {
                call.respond(HttpStatusCode.OK)
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.onRejectInvitation() = guardContent {
        withToken { token ->
            val isRejected = usersService.rejectInvitation(call.receive(), token)
            if (!isRejected) {
                call.respond(HttpStatusCode.Conflict)
            } else {
                call.respond(HttpStatusCode.OK)
            }
        }
    }

    private suspend fun DefaultWebSocketServerSession.onSignalling() {
        println("SOCKET CONNECTION:")
        call.tokenOrNull()?.let {
            sigService.onConnection(it, this)
        } ?: close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Token not found !"))
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.guardContent(
            block: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit
    ) = try {
        block()
    } catch (ex: ContentTransformationException) {
        call.respond(HttpStatusCode.BadRequest)
    }

    private fun ApplicationCall.tokenOrNull(): UUID? {
        val principal = principal<SupnetPrincipal>() ?: return null
        return principal.token
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.withToken(
            block: suspend PipelineContext<Unit, ApplicationCall>.(token: UUID) -> Unit
    ) {
        val token = call.tokenOrNull()
        if (token == null) {
            call.respond(HttpStatusCode.Forbidden)
        } else {
            block(token)
        }
    }
}


