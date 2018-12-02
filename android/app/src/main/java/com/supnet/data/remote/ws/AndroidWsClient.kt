package com.supnet.data.remote.ws

import com.jakewharton.rxrelay2.PublishRelay
import com.supnet.data.FriendshipInvitation
import com.supnet.data.UserState
import com.supnet.data.UserState.*
import com.supnet.data.remote.ws.WsEvent.*
import com.supnet.data.remote.ws.WsEvent.WsMessageEvent.*
import com.supnet.device.connection.ConnectionState
import com.supnet.device.connection.ConnectionState.*
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.withLatestFrom
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class AndroidWsClient(
    private val buildWs: (token: String, WebSocketListener) -> WebSocket,
    private val userStates: Observable<UserState>,
    private val connectionStates: Observable<ConnectionState>
) : WsClient {

    private val invitationIntents = PublishRelay.create<FriendshipInvitation>()

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

        val intents = invitationIntents
            .withLatestFrom(statesStream)
            .flatMap { (invitation, wsState) ->
                return@flatMap when (wsState) {
                    is WsStateEvent.WsOpen -> {
                        if (wsState.ws.send("")) Observable.just(InvitationSending)
                        else Observable.just(InvitationError)
                    }
                    WsStateEvent.WsClosed -> Observable.just(InvitationError)
                }
            }

        val rawMessages = socketStream
            .flatMap { return@flatMap when (it) {
                is WsMessageEvent -> Observable.just(it)
                else -> Observable.empty()
            }}

        return@lazy Observable.merge(intents, rawMessages)
    }

    override fun socketStates() = statesStream

    override fun socketMessages(): Observable<WsEvent.WsMessageEvent> = socketMessages

    override fun sendInvitation(invitation: FriendshipInvitation) = invitationIntents.accept(invitation)

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