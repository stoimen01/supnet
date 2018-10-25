package com.supnet.signaling

import com.google.protobuf.InvalidProtocolBufferException
import com.supnet.signaling.SignalingClient.RoomsEvent.*
import com.supnet.signaling.SignalingClient.SignalingState.*
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import okhttp3.*
import okio.ByteString
import proto.CreateRoomIntent
import proto.SignalingEvent
import proto.SignalingEvent.EventCase.*
import proto.SignalingIntent
import java.nio.ByteBuffer

class SignalingClient(
    private val client: OkHttpClient
) : WebSocketListener(), CookieJar {

    sealed class SignalingState {
        object Idle : SignalingState()
        object Connecting : SignalingState()
        object Connected : SignalingState()
        object Closed : SignalingState()
    }

    sealed class RoomsEvent {
        data class RoomsReceived(val rooms: List<String>) : RoomsEvent()
        object RoomCreated : RoomsEvent()
        object RoomCreating : RoomsEvent()
        object RoomNotCreated : RoomsEvent()
    }

    private val states = BehaviorSubject.createDefault<SignalingState>(Idle)
    private val events = PublishSubject.create<RoomsEvent>()

    lateinit var ws: WebSocket

    fun getStates(): Observable<SignalingState> = states

    fun getEvents(): Observable<RoomsEvent> = events

    fun connect() {
        val request = Request.Builder()
            .url("ws://10.0.2.2:8080/signaling")
            .build()

        ws = client.newWebSocket(request, this)
        states.onNext(Connecting)
    }

    fun close() {
        ws.close(0, "")
    }

    /* Websocket listener implementation */
    override fun onOpen(webSocket: WebSocket, response: Response) {
        states.onNext(Connected)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        try {
            processEvent(SignalingEvent.parseFrom(bytes.toByteArray()))
        } catch (ex : InvalidProtocolBufferException) {
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        states.onNext(Closed)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        t.printStackTrace()
        states.onNext(Closed)
    }

    private fun processEvent(event: SignalingEvent) = when (event.eventCase) {
        ROOMS_INFO -> {
            val rooms = event.roomsInfo.listList.map { it.name }
            events.onNext(RoomsReceived(rooms))
        }
        ROOM_CREATED -> {
            events.onNext(RoomCreated)
        }
        ROOM_NOT_CREATED -> {
            events.onNext(RoomNotCreated)
        }
        ROOM_JOINED -> {

        }
        ROOM_NOT_JOINED -> {

        }
        ROOM_LEAVED -> {

        }
        ROOM_NOT_LEAVED -> {

        }
        ROOM_REMOVED -> {

        }
        EVENT_NOT_SET, null -> { }
    }

    /* Cookie jar implementation */
    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {

    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        return mutableListOf()
    }

    fun createRoom(name: String) {

        val intent = SignalingIntent.newBuilder()
            .setCreateRoom(CreateRoomIntent.newBuilder().setRoomName(name))
            .build()

        if (ws.send(ByteString.of(ByteBuffer.wrap(intent.toByteArray())))) {
            events.onNext(RoomCreating)
        } else {
            events.onNext(RoomNotCreated)
        }
    }


}