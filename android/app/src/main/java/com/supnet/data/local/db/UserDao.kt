package com.supnet.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserRowById(id: String): Observable<UserRow>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserRow(user: UserRow): Completable

    @Query("DELETE FROM users")
    fun deleteAllUserRows(): Single<Int>

}