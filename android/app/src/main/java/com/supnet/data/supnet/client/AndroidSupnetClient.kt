package com.supnet.data.supnet.client

import com.supnet.data.supnet.*
import com.supnet.data.supnet.client.rest.SupnetRestClient
import com.supnet.data.supnet.client.socket.SupnetSocketClient
import io.reactivex.Completable

class AndroidSupnetClient(
    private val restClient: SupnetRestClient,
    private val socketClient: SupnetSocketClient
) : SupnetClient {

    override fun signUp(email: String, userName: String, password: String) =
        restClient.signUp(email, userName, password)

    override fun signOff() = restClient.signOff()

    override fun signIn(email: String, password: String) =
        restClient.signIn(email, password)

    override fun signOut() = restClient.signOut()

    override fun sendInvitation(invitation: FriendshipInvitation): Completable {
        return socketClient
            .sendInvitation(invitation)
            .onErrorResumeNext {
                restClient.sendInvitation(invitation)
            }
    }

    override fun openSocket(token: String) = socketClient.openSocket(token)

}