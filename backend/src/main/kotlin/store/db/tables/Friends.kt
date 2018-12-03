package store.db.tables

import org.jetbrains.exposed.sql.Table

object Friends : Table() {
    val user = integer("user")
    val friend = integer("friend")
}