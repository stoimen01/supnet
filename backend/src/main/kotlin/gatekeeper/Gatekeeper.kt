package gatekeeper

import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.auth.principal
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
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
import org.jetbrains.exposed.sql.*
import signaling.SignalingManager
import java.util.*

class Gatekeeper(private val signalingManager: SignalingManager) {

    private val tokens = mutableListOf<UUID>()

    fun Routing.openGates() {
        authenticate()
        post("/signup") { onSignUp() }
        post("/signin") { onSignIn() }
        post("/invitation") { onInvitation() }
        webSocket("/signaling") { onSignalling() }
    }

    private fun Route.authenticate() {
        val bearer = "Bearer "
        intercept(ApplicationCallPipeline.Features) {
            val authorization = call.request.header(HttpHeaders.Authorization) ?: return@intercept
            if (!authorization.startsWith(bearer)) return@intercept
            val token = authorization.removePrefix(bearer).trim()
            call.authentication.principal(SupnetPrincipal(token))
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.onSignUp() = guardContent {
        val result = signUp(call.receive())
        if (result == null) {
            call.respond(HttpStatusCode.Conflict)
        } else {
            call.respond(HttpStatusCode.Created, result)
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.onSignIn() = guardContent {
        val result = signIn(call.receive())
        if (result == null) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            call.respond(HttpStatusCode.OK, result)
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.onInvitation() = guardContent {
        val principal = call.validatePrincipal()
        if (principal == null) {
            call.respond(HttpStatusCode.Forbidden)
        } else {
            val isSent = sendInvitation(call.receive())
            if (!isSent) {
                call.respond(HttpStatusCode.Conflict)
            } else {
                call.respond(HttpStatusCode.OK)
            }
        }
    }

    private suspend fun DefaultWebSocketServerSession.onSignalling() {
        val principal = call.validatePrincipal()
        if (principal == null) {
            println("WS SESSION CLOSED")
            close(null)
        } else {
            println("WS SESSION PASSED")
            //signalingManager.onNewSession(session.id, wsSession)
        }
    }

    private fun ApplicationCall.validatePrincipal(): SupnetPrincipal? {
        val principal = principal<SupnetPrincipal>() ?: return null
        synchronized(tokens) {
            val uuid = UUID.fromString(principal.token)
            return if (tokens.contains(uuid)) {
                principal
            } else {
                null
            }
        }
    }

    suspend fun getAllUsers(): List<User> = trans {
        Users.selectAll().map(::toUser)
    }

    suspend fun getUser(id: Int): User? = trans {
        Users.select { (Users.id eq id) }.mapNotNull(::toUser).singleOrNull()
    }

    private suspend fun signUp(info: SignUpInfo): SignResult? = trans {

        val id = Users.insert {
            it[name] = info.name
            it[email] = info.email
            it[password] = info.password
        } get Users.id

        val token = UUID.randomUUID()

        synchronized(tokens) { tokens.add(token) }

        return@trans SignResult(id!!, token.toString(), info.name, listOf(), listOf(), listOf())
    }

    private suspend fun signIn(signInInfo: SignInInfo): SignResult? = trans {
        val (email, password) = signInInfo

        val user = Users
                .select { (Users.email eq email) and (Users.password eq password) }
                .map(::toUser)
                .singleOrNull()

        if (user != null) {

            val friends = Friends
                    .select { Friends.user eq user.id }
                    .mapNotNull {
                        val friendId = it[Friends.friend]
                        Users.select { Users.id eq friendId }
                                .map(::toUser)
                                .singleOrNull()
                    }
                    .map { Friend(it.name) }

            val invitations = Invitations
                    .select { Invitations.recipientName eq user.name }
                    .map(::toInvitation)

            val token = UUID.randomUUID()
            synchronized(tokens) { tokens.add(token) }
            return@trans SignResult(user.id, token.toString(), user.name, friends, listOf(), invitations)
        } else {
            return@trans null
        }
    }

    private suspend fun sendInvitation(invitation: FriendshipInvitation) = trans {
        val (iName, rName, msg) = invitation

        val recipientUser = Users
                .select { Users.name eq rName }
                .map(::toUser)
                .singleOrNull() ?: return@trans false

        Invitations.insert {
            it[recipientName] = rName
            it[initiatorName] = iName
            it[message] = msg
        }

        return@trans true
    }

    private suspend fun removeAccount(id: Int) = trans {
        // todo clean friends table
        Users.deleteWhere { Users.id eq id } > 0
    }

    private fun toUser(row: ResultRow) = User(
            id = row[Users.id],
            name = row[Users.name],
            email = row[Users.email],
            password = row[Users.password]
    )

    private fun toInvitation(row: ResultRow) = FriendshipInvitation(
            initiatorName = row[Invitations.initiatorName],
            recipientName = row[Invitations.recipientName],
            message = row[Invitations.message]
    )

    private suspend fun PipelineContext<Unit, ApplicationCall>.guardContent(
            block: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit
    ) = try {
        block()
    } catch (ex: ContentTransformationException) {
        call.respond(HttpStatusCode.BadRequest)
    }

}


