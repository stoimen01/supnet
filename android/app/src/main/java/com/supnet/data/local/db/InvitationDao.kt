package com.supnet.data.local.db

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface InvitationDao {

    @Query("SELECT * FROM invitations")
    fun getInvitations(): Observable<List<InvitationRow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInvitation(invitationRow: InvitationRow): Completable

    @Insert
    fun insertAll(invitations: List<InvitationRow>): Completable

    @Query("DELETE FROM invitations WHERE id = :id")
    fun deleteInvitation(id: Int)

    @Query("DELETE FROM invitations")
    fun deleteAllInvitations()

}