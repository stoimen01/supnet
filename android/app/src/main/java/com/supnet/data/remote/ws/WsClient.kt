package com.supnet.data.remote.ws

import com.supnet.data.FriendshipInvitation
import io.reactivex.Observable

interface WsClient {

    fun socketStates(): Observable<WsEvent.WsStateEvent>

    fun socketMessages(): Observable<WsEvent.WsMessageEvent>

    fun sendInvitation(invitation: FriendshipInvitation)

}