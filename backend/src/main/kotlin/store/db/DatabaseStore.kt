package store.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import Friend
import store.SupnetStore
import User
import UserID
import store.db.tables.Friends
import store.db.tables.Invitations
import store.db.tables.Users
import Invitation

class DatabaseStore : SupnetStore {

    private val dispatcher = newFixedThreadPoolContext(5, "database-pool")

    init {
        Database.connect(hikari())
        transaction {
            SchemaUtils.create(Users, Friends, Invitations)
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.h2.Driver"
            jdbcUrl = "jdbc:h2:mem:test"
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }

    private suspend inline fun <T> query(crossinline block: () -> T): T = withContext(dispatcher) { transaction { block() } }

    override suspend fun containsEmail(email: String) = query {
        Users.select { Users.email eq email }.count() > 0
    }

    override suspend fun addUser(name: String, email: String, password: String) = query {
        (Users.insert {
            it[Users.name] = name
            it[Users.email] = email
            it[Users.password] = password
        } get Users.id)!!
    }

    override suspend fun getUserById(id: UserID) = query {
        Users.select { Users.id eq id }.map(::toUser).singleOrNull()
    }

    override suspend fun removeUser(id: Int) = query {
        Users.deleteWhere { Users.id eq id } > 0
    }

    override suspend fun getUserByCredentials(email: String, password: String) = query {
        Users.select { (Users.email eq email) and (Users.password eq password) }.map(::toUser).singleOrNull()
    }

    override suspend fun getUserFriends(id: Int) = query {
        Friends
                .select { Friends.user eq id }
                .mapNotNull {
                    val friendId = it[Friends.friend]
                    Users.select { Users.id eq friendId }
                            .map(::toUser)
                            .singleOrNull()
                }
                .map { Friend(it.name) }
    }

    override suspend fun getUserByName(name: String) = query {
        Users.select { Users.name eq name }.map(::toUser).singleOrNull()
    }

    override suspend fun addInvitation(initiatorId: Int, recipientId: Int, msg: String) = query {
        (Invitations.insert {
            it[initiator] = initiatorId
            it[recipient] = recipientId
            it[message] = msg
        } get Invitations.id)!!
    }

    override suspend fun getInvitationsByRecipient(recipientId: UserID) = query {
        Invitations.select { Invitations.recipient eq recipientId }.map(::toInvitation)
    }

    private fun toUser(row: ResultRow) = User(
            id = row[Users.id],
            name = row[Users.name],
            email = row[Users.email],
            password = row[Users.password]
    )


    private fun toInvitation(row: ResultRow) = Invitation(
            id = row[Invitations.id],
            initiatorId = row[Invitations.initiator],
            recipientId = row[Invitations.recipient],
            message = row[Invitations.message]
    )

}