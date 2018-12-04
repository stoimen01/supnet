package com.supnet.data.remote.ws

import io.reactivex.Observable

interface WsClient {

    fun socketStates(): Observable<WsEvent.WsStateEvent>

    fun socketMessages(): Observable<WsEvent.WsMessageEvent>

}