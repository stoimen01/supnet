package com.supnet.data.local

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.supnet.common.Nullable
import com.supnet.domain.Friend
import com.supnet.domain.Invitation
import com.supnet.data.local.db.FriendRow
import com.supnet.data.local.db.InvitationRow
import com.supnet.data.local.db.SupnetDatabase
import com.supnet.data.local.db.UserRow
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class AndroidSupnetStore(
    private val db: SupnetDatabase,
    private val sp: SharedPreferences
) : SupnetStore {

    @SuppressLint("ApplySharedPref")
    override fun saveToken(token: String): Completable = Completable.fromCallable {
        with(sp.edit()) {
            putString(SP_TOKEN_KEY, token)
            commit()
        }
    }

    override fun getToken(): Single<Nullable<String>> = Single.fromCallable {
        Nullable(sp.getString(SP_TOKEN_KEY, null))
    }

    override fun saveUserData(id: Int, name: String, email: String, password: String) =
        db.userDao().insertUserRow(UserRow(id, name, email, password))

    override fun removeUserData(): Completable {
        return db.userDao()
            .deleteAllUserRows()
            .andThen(db.friendDao().deleteAllFriendRows())
    }

    override fun addFriend(friend: Friend): Completable {
        return db.friendDao()
            .insertFriendRow(FriendRow(friend.id, friend.name))
    }

    override fun saveFriends(friends: List<Friend>): Completable {
        return db.friendDao()
            .insertAll(friends.map { FriendRow(it.id, it.name) })
    }

    override fun friends(): Observable<Friend> {
        return db.friendDao()
            .getFriends()
            .map { Friend(it.id, it.name) }
    }

    override fun addInvitation(invitation: Invitation): Completable {
        return db.invitationDao()
            .insertInvitation(InvitationRow(invitation.id, invitation.senderName, invitation.message))
    }

    override fun invitations(): Observable<Invitation> {
        return db.invitationDao()
            .getInvitations()
            .map { Invitation(it.id, it.senderName, it.message) }
    }

    companion object {
        const val SP_TOKEN_KEY = "SP_TOKEN_KEY"
    }

}