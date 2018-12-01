package com.supnet.data.supnet.client

import com.supnet.data.supnet.FriendshipInvitation
import com.supnet.data.supnet.SignResult
import com.supnet.data.supnet.client.socket.SocketEvent
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface SupnetClient {

    fun signUp(email: String, userName: String, password: String): Single<SignResult>

    fun signOff(): Completable

    fun signIn(email: String, password: String): Single<SignResult>

    fun signOut(): Completable

    fun sendInvitation(invitation: FriendshipInvitation): Completable

    fun openSocket(token: String): Observable<SocketEvent>

}