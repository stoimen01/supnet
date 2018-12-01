package com.supnet.data.supnet.client.socket

import com.supnet.data.supnet.FriendshipInvitation
import com.supnet.data.supnet.SocketState
import com.supnet.data.supnet.client.socket.SocketEvent.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import okhttp3.*
import okio.ByteString

class AndroidSocketClient(
    private val client: OkHttpClient,
    private val url: String
) : SupnetSocketClient {

    override fun openSocket(token: String): Observable<SocketEvent> {
        return Observable.create { emitter ->
            val request = Request.Builder()
                .addHeader("Authorization: Bearer ", token)
                .url(url)
                .build()
            val ws = client.newWebSocket(request, WsListener(emitter))
            emitter.setCancellable(ws::cancel)
        }
    }

    override fun sendInvitation(invitation: FriendshipInvitation): Completable {
        return Completable.complete()
    }

    private class WsListener(
        private val emitter: ObservableEmitter<SocketEvent>
    ) : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            emitter.onNext(StateEvent(SocketState.OPEN))
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            emitter.onNext(StateEvent(SocketState.OPENING))
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            emitter.onError(t)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            emitter.onNext(MessageEvent(bytes))
        }
    }

}