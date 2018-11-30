package gatekeeper

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val password = varchar("password", 255)
}

object Friends : Table() {
    val user = integer("user")
    val friend = integer("friend")
}

data class User(
        val id: Int,
        val name: String,
        val email: String,
        val password: String
)

data class SignUpInfo(
        val name: String,
        val email: String,
        val password: String
)

data class SignInInfo(
        val email: String,
        val password: String
)

data class SignResult(
        val id: Int,
        val username: String,
        val friends: List<Friend>,
        val gadgets: List<Gadget>
)

data class Friend(val name: String)

data class Gadget(val name: String, val owner: String)