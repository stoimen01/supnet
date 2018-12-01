package com.supnet.data.supnet.client.socket

import com.supnet.data.supnet.FriendshipInvitation
import com.supnet.data.supnet.SocketState
import io.reactivex.Completable
import io.reactivex.Observable

interface SupnetSocketClient {

    fun openSocket(token: String): Observable<SocketEvent>

    fun sendInvitation(invitation: FriendshipInvitation): Completable

}