package com.supnet.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface FriendDao {

    @Query("SELECT * FROM friends")
    fun getFriends(): Observable<List<FriendRow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFriendRow(friend: FriendRow): Completable

    @Insert
    fun insertAll(friends: List<FriendRow>): Completable

    @Query("DELETE FROM friends")
    fun deleteAllFriendRows()

}