package com.supnet.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserRow::class, FriendRow::class, InvitationRow::class], version = 1)
abstract class SupnetDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun friendDao(): FriendDao

    abstract fun invitationDao(): InvitationDao

    companion object {

        @Volatile private var INSTANCE: SupnetDatabase? = null

        fun getInstance(context: Context): SupnetDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                SupnetDatabase::class.java, "Supnet.db"
            ).build()
    }
}