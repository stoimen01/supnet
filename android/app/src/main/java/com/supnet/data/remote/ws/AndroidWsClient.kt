package com.supnet.data.remote.ws

import com.supnet.data.UserState
import com.supnet.data.UserState.*
import com.supnet.data.remote.ws.WsEvent.*
import com.supnet.device.connection.ConnectionState
import com.supnet.device.connection.ConnectionState.*
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.rxkotlin.Observables
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class AndroidWsClient(
    private val buildWs: (token: String, WebSocketListener) -> WebSocket,
    private val userStates: Observable<UserState>,
    private val connectionStates: Observable<ConnectionState>
) : WsClient {

    // source of messages and states combined
    private val socketStream by lazy {
        Observables.combineLatest(userStates, connectionStates)
            .switchMap { (usrState, connState) ->
                return@switchMap when (usrState) {
                    is SignedIn -> when (connState) {
                        CONNECTED -> openSocket(usrState.token)
                        DISCONNECTED -> Observable.empty()
                    }
                    else -> Observable.empty()
                }
            }
            .retryWhen { errors ->
                errors.flatMap { Observable.timer(5, TimeUnit.SECONDS) }
            }
    }

    private val statesStream by lazy {
        socketStream
            .flatMap { return@flatMap when (it) {
                is WsStateEvent -> Observable.just(it)
                else -> Observable.empty()
            }}
            .replay(1)
            .refCount()
    }

    private val socketMessages by lazy {
       socketStream
            .flatMap { return@flatMap when (it) {
                is WsMessageEvent -> Observable.just(it)
                else -> Observable.empty()
            }}
    }

    override fun socketStates() = statesStream

    override fun socketMessages(): Observable<WsEvent.WsMessageEvent> = socketMessages


    private fun openSocket(token: String): Observable<WsEvent> {
        return Observable.create { emitter ->
            val ws = buildWs(token, WsListener(emitter))
            emitter.setCancellable(ws::cancel)
        }
    }

    private class WsListener(
        private val emitter: ObservableEmitter<WsEvent>
    ) : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            if (!emitter.isDisposed) {
                emitter.onNext(WsStateEvent.WsOpen(webSocket))
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            if (!emitter.isDisposed) {
                emitter.onNext(WsStateEvent.WsClosed)
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            if (!emitter.isDisposed) {
                emitter.onNext(WsStateEvent.WsClosed)
                emitter.onError(t)
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            if (!emitter.isDisposed) {
               // emitter.onNext()
            }
        }
    }

}