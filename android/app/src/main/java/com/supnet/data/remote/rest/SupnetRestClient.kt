package com.supnet.data.remote.rest

import com.supnet.data.FriendshipInvitation
import com.supnet.data.SignResult
import io.reactivex.Completable
import io.reactivex.Single

interface SupnetRestClient {

    fun signUp(email: String, userName: String, password: String): Single<SignResult>

    fun signOff(token: String): Completable

    fun signIn(email: String, password: String): Single<SignResult>

    fun signOut(token: String): Completable

    fun sendInvitation(token: String, invitation: FriendshipInvitation): Completable

}