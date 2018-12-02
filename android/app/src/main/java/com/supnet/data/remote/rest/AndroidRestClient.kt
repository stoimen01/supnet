package com.supnet.data.remote.rest

import com.supnet.data.FriendshipInvitation
import com.supnet.data.SignResult
import io.reactivex.Completable
import io.reactivex.Single

class AndroidRestClient(
    private val supnetRestApi: SupnetRestApi
): SupnetRestClient {

    override fun signUp(email: String, userName: String, password: String): Single<SignResult> =
        supnetRestApi.signUp(RegisterCredentials(userName, email, password))
            .map { it.body()!! }

    override fun signOff(token: String): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun signIn(email: String, password: String): Single<SignResult>  =
        supnetRestApi.signIn(LoginCredentials(email, password))
            .map { it.body()!! }

    override fun signOut(token: String): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendInvitation(token: String, invitation: FriendshipInvitation): Completable =
        supnetRestApi.sendInvitation(invitation)

}