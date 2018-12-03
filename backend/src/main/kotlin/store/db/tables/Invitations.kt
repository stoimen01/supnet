package store.db.tables

import org.jetbrains.exposed.sql.Table

object Invitations : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val initiator = integer("initiator")
    val recipient = integer("recipient")
    val message = varchar("message", 255)
}