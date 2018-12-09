package com.supnet.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class FriendRow(
    @PrimaryKey val id: Int = 0,
    val name: String = ""
)