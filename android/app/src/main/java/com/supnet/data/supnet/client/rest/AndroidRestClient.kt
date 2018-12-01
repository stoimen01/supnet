package com.supnet.data.supnet.client.rest

import com.supnet.data.supnet.FriendshipInvitation
import com.supnet.data.supnet.SignResult
import io.reactivex.Completable
import io.reactivex.Single

class AndroidRestClient(
    private val supnetRestApi: SupnetRestApi
): SupnetRestClient {

    override fun signUp(email: String, userName: String, password: String): Single<SignResult> =
        supnetRestApi.signUp(RegisterCredentials(userName, email, password))
            .map { it.body()!! }

    override fun signOff(): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun signIn(email: String, password: String): Single<SignResult>  =
        supnetRestApi.signIn(LoginCredentials(email, password))
            .map { it.body()!! }

    override fun signOut(): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendInvitation(invitation: FriendshipInvitation): Completable =
        supnetRestApi.sendInvitation(invitation)

}