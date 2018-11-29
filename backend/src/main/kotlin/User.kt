import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val password = varchar("password", 255)
}

data class User(
        val id: Int,
        val name: String,
        val email: String,
        val password: String
)

data class NewUser(
        val name: String,
        val email: String,
        val password: String
)