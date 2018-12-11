package com.supnet.data.local

import com.supnet.common.Nullable
import com.supnet.domain.entities.Friend
import com.supnet.domain.entities.Invitation
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface SupnetStore {

    fun saveToken(token: String): Completable

    fun getToken(): Single<Nullable<String>>

    fun saveUserData(id: Int, name: String, email: String, password: String): Completable

    fun removeUserData(): Completable

    fun addFriend(friend: Friend): Completable

    fun saveFriends(friends: List<Friend>): Completable

    fun friends(): Observable<List<Friend>>

    fun addInvitation(invitation: Invitation): Completable

    fun removeInvitation(id: Int): Completable

    fun invitations(): Observable<List<Invitation>>

}