package com.supnet.data.remote.ws

import com.google.protobuf.InvalidProtocolBufferException
import com.jakewharton.rxrelay2.PublishRelay
import com.supnet.data.remote.ws.AndroidWsClient.WsListenerEvent.StateEvent.*
import com.supnet.domain.user.UserState
import com.supnet.data.remote.ws.WsEvent.*
import com.supnet.device.connection.ConnectionState
import com.supnet.device.connection.ConnectionState.*
import io.reactivex.Observable
import io.reactivex.Observable.*
import io.reactivex.ObservableEmitter
import io.reactivex.rxkotlin.Observables
import okhttp3.*
import okio.ByteString
import proto.WsEvent.EventCase.*
import java.util.concurrent.TimeUnit

class AndroidWsClient(
    private val buildWs: (token: String, WebSocketListener) -> WebSocket,
    private val userStates: Observable<UserState>,
    private val connectionStates: Observable<ConnectionState>
) : WsClient {

    private sealed class WsListenerEvent {

        sealed class StateEvent : WsListenerEvent() {
            data class Opened(val webSocket: WebSocket) : StateEvent()
            object Connecting : StateEvent()
            object Closed : StateEvent()
            object Failed : StateEvent()
        }

        data class MessageEvent(val bytes: ByteString) : WsListenerEvent()

    }

    private val messages = PublishRelay.create<WsMessage>()

    override fun sendMessage(msg: WsMessage) = messages.accept(msg)

    // source of messages and states combined
    private val socketStream by lazy {
        Observables.combineLatest(userStates, connectionStates)
            .switchMap { (usrState, connState) ->
                val token = usrState.token() ?: return@switchMap empty<WsListenerEvent>()
                return@switchMap when (connState) {
                    CONNECTED -> openSocket(token)
                    DISCONNECTED -> empty()
                }
            }
            .retryWhen { errors ->
                errors.flatMap {
                    it.printStackTrace()
                    timer(5, TimeUnit.SECONDS)
                }
            }
    }

    // states only stream
    private val statesStream by lazy {
        socketStream
            .flatMap { return@flatMap when (it) {
                is WsListenerEvent.StateEvent -> just(it)
                else -> empty()
            }}
            .replay(1)
            .refCount()
    }

    // events only stream
    private val events by lazy {

        /*messages.flatMap {
            statesStream.flatMap { state ->
                return@flatMap when (state) {
                    is Opened -> {
                        //state.webSocket.send()
                    }
                    else -> {
                        //Observable.empty<>()
                    }
                }
            }
        }*/

        socketStream
            .flatMap { return@flatMap when (it) {
                is WsListenerEvent.MessageEvent -> tryParseMessage(it.bytes)
                else -> empty<WsEvent>()
            }}
    }

    // mapping internal state to public
    override fun states(): Observable<WsState> = statesStream.map {
        return@map when (it) {
            Connecting -> WsState.CONNECTING
            is Opened -> WsState.CONNECTED
            Closed, Failed -> WsState.DISCONNECTED
        }
    }

    override fun events(): Observable<WsEvent> = events

    private fun openSocket(token: String): Observable<WsListenerEvent> {
        return create { emitter ->
            val ws = buildWs(token, WsListener(emitter))
            emitter.setCancellable(ws::cancel)
            emitter.onNext(Connecting)
        }
    }

    private class WsListener(
        private val emitter: ObservableEmitter<WsListenerEvent>
    ) : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            if (emitter.isDisposed) return
            emitter.onNext(Opened(webSocket))
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            if (emitter.isDisposed) return
            emitter.onNext(Closed)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            if (emitter.isDisposed) return
            emitter.onNext(Failed)
            emitter.onError(t)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            if (emitter.isDisposed) return
            emitter.onNext(WsListenerEvent.MessageEvent(bytes))
        }

    }

    private fun tryParseMessage(bytes: ByteString): Observable<WsEvent> {
        return try {
            mapEvent(proto.WsEvent.parseFrom(bytes.toByteArray()))
        } catch (ex: InvalidProtocolBufferException) {
            ex.printStackTrace()
            empty<WsEvent>()
        }
    }

    private fun mapEvent(wsEvent: proto.WsEvent): Observable<WsEvent> = when (wsEvent.eventCase) {

        INVITATION -> with(wsEvent.invitation) {
            just<WsEvent>(InvitationReceived(
                invitationId = invitationId,
                senderName = senderName,
                message = message
            ))
        }

        INVITATION_ACCEPTED -> with(wsEvent.invitationAccepted) {
            just<WsEvent>(InvitationAccepted(
                invitationId = invitationId,
                friendId = friendId,
                friendName = friendName
            ))
        }

        EVENT_NOT_SET, null -> empty()
    }

}