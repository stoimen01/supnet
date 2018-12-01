package com.supnet.data.supnet.client.rest

import com.supnet.data.supnet.FriendshipInvitation
import com.supnet.data.supnet.SignResult
import io.reactivex.Completable
import io.reactivex.Single

interface SupnetRestClient {

    fun signUp(email: String, userName: String, password: String): Single<SignResult>

    fun signOff(): Completable

    fun signIn(email: String, password: String): Single<SignResult>

    fun signOut(): Completable

    fun sendInvitation(invitation: FriendshipInvitation): Completable

}