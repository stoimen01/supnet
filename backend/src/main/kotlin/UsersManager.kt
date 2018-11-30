import io.ktor.application.ApplicationCall
import io.ktor.auth.principal
import io.ktor.websocket.DefaultWebSocketServerSession
import org.jetbrains.exposed.sql.*
import signaling.SignalingManager
import java.util.*

class UsersManager(private val signalingManager: SignalingManager) {

    private val tokens = mutableListOf<UUID>()

    suspend fun getAllUsers(): List<User> = dbQuery {
        Users.selectAll().map(::toUser)
    }

    suspend fun getUser(id: Int): User? = dbQuery {
        Users.select { (Users.id eq id) }.mapNotNull(::toUser).singleOrNull()
    }

    suspend fun addUser(user: NewUser): UUID? = dbQuery {

        Users.insert {
            it[name] = user.name
            it[email] = user.email
            it[password] = user.password
        }

        val token = UUID.randomUUID()

        synchronized(tokens) { tokens.add(token) }

        return@dbQuery token
    }

    suspend fun loginUser(loginCredentials: LoginCredentials): UUID? = dbQuery {
        val (email, password) = loginCredentials

        val result = Users.select {
            (Users.email eq email) and (Users.password eq password)
        }.singleOrNull()

        if (result == null) {
            return@dbQuery null
        } else {
            val token = UUID.randomUUID()
            synchronized(tokens) { tokens.add(token) }
            return@dbQuery token
        }
    }

    suspend fun removeUser(id: Int) = dbQuery {
        Users.deleteWhere { Users.id eq id } > 0
    }

    private fun toUser(row: ResultRow): User = User(
            id = row[Users.id],
            name = row[Users.name],
            email = row[Users.email],
            password = row[Users.password]
    )

    suspend fun onWsSession(wsSession: DefaultWebSocketServerSession) {
        val principal = wsSession.call.validatePrincipal()
        if (principal == null) {
            println("WS SESSION CLOSED")
            wsSession.close(null)
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

}


