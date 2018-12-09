package com.supnet.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invitations")
data class InvitationRow(
    @PrimaryKey val id: Int = 0,
    val senderName: String = "",
    val message: String = ""
)