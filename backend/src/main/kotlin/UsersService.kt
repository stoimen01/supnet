import DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class UsersService {

    suspend fun getAllUsers(): List<User> = dbQuery {
        Users.selectAll().map(::toUser)
    }

    suspend fun getUser(id: Int): User? = dbQuery {
        Users.select { (Users.id eq id) }.mapNotNull(::toUser).singleOrNull()
    }

    suspend fun addUser(user: NewUser) = dbQuery {
        Users.insert {
            it[name] = user.name
            it[email] = user.email
            it[password] = user.password
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

}