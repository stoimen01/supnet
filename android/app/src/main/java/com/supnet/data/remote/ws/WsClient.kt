package com.supnet.data.remote.ws

import io.reactivex.Observable

interface WsClient {

    fun sendMessage(msg: WsMessage)

    fun events(): Observable<WsEvent>

    fun states(): Observable<WsState>
}