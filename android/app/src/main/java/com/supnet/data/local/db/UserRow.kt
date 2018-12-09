package com.supnet.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserRow(
    @PrimaryKey val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val password: String = ""
)