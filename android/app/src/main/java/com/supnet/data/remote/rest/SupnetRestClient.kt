package com.supnet.data.remote.rest

import com.supnet.data.*
import io.reactivex.Completable
import io.reactivex.Single

interface SupnetRestClient {

    fun signUp(email: String, userName: String, password: String): Single<SignUpResponse>

    fun signOff(token: String): Completable

    fun signIn(email: String, password: String): Single<SignInResponse>

    fun signOut(token: String): Completable

    fun sendInvitation(token: String, invitation: InvitationRequest): Completable

    fun acceptInvitation(token: String, request: AcceptInvitationRequest): Completable

}