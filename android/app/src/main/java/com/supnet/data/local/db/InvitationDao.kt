package com.supnet.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface InvitationDao {

    @Query("SELECT * FROM invitations")
    fun getInvitations(): Observable<InvitationRow>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInvitation(invitationRow: InvitationRow): Completable

    @Insert
    fun insertAll(invitations: List<InvitationRow>): Completable

    @Query("DELETE FROM invitations")
    fun deleteAllInvitations(): Single<Int>

}