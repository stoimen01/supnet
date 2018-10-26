package com.supnet.signaling.client

import com.google.protobuf.InvalidProtocolBufferException
import com.supnet.signaling.entities.Room
import com.supnet.signaling.client.RxSignalingClient.RoomsEvent.*
import com.supnet.signaling.client.RxSignalingClient.RoomsEvent.RoomJoinEvent.*
import com.supnet.signaling.client.RxSignalingClient.SignalingState.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import okhttp3.*
import okio.ByteString
import proto.*
import proto.SignalingEvent.EventCase.*
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeUnit

class RxSignalingClient(
    private val client: OkHttpClient
) : WebSocketListener(), SignalingClient {

    sealed class SignalingState {
        object Idle : SignalingState()
        object Connecting : SignalingState()
        object Connected : SignalingState()
        object Closed : SignalingState()
    }

    sealed class RoomsEvent {
        data class RoomsReceived(val rooms: List<Room>) : RoomsEvent()
        data class RoomCreated(val room: Room) : RoomsEvent()
        data class RoomRemoved(val roomId: UUID) : RoomsEvent()
        object RoomNotCreated : RoomsEvent()

        sealed class RoomJoinEvent : RoomsEvent() {
            object RoomJoined : RoomJoinEvent()
            object RoomNotJoined : RoomJoinEvent()
        }
    }

    private val states = BehaviorSubject.createDefault<SignalingState>(Idle)
    private val events = PublishSubject.create<RoomsEvent>()
    private val rooms = createRoomsList()

    private lateinit var ws: WebSocket

    fun getStates(): Observable<SignalingState> = states

    fun getRooms() = rooms

    fun getRoom(roomId: UUID): Observable<Room> {
        return rooms
            .flatMapIterable { it }
            .filter { it.id == roomId }
    }

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

    fun createRoom(name: String): Single<UUID> {

        val intent = CreateRoomIntent.newBuilder()
            .setRoomName(name)

        return events
            .doOnSubscribe { sendIntent { setCreateRoom(intent) } }
            .flatMap { event ->
                if (event is RoomCreated) {
                    if (event.room.name == name) {
                        Observable.just(event.room.id)
                    } else {
                        Observable.empty()
                    }
                } else if (event is RoomNotCreated) {
                    Observable.error(Throwable("Not ready"))
                } else {
                    Observable.empty()
                }
            }
            .timeout(10, TimeUnit.SECONDS)
            .take(1)
            .singleOrError()
    }

    fun joinRoom(roomId: UUID): Completable {
        val intent = JoinRoomIntent.newBuilder()
            .setRoomId(roomId.toString())

        return events
            .doOnSubscribe { sendIntent { setJoinRoom(intent) } }
            .ofType(RoomJoinEvent::class.java)
            .take(1)
            .flatMapCompletable {
                return@flatMapCompletable when (it) {
                    RoomJoined -> Completable.complete()
                    RoomNotJoined -> Completable.error(Throwable("Room join error"))
                }
            }
    }

    fun leaveRoom(roomId: UUID) {

        val intent = LeaveRoomIntent.newBuilder()
            .setRoomId(roomId.toString())

        sendIntent {
            setLeaveRoom(intent)
        }
    }

    /* Websocket listener implementation */
    override fun onOpen(webSocket: WebSocket, response: Response) {
        states.onNext(Connected)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        try {
            processEvent(SignalingEvent.parseFrom(bytes.toByteArray()))
        } catch (ex : InvalidProtocolBufferException) {
            ex.printStackTrace()
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
            val rooms = event.roomsInfo.listList.map {
                Room(
                    UUID.fromString(it.id),
                    it.name
                )
            }
            events.onNext(RoomsReceived(rooms))
        }
        ROOM_CREATED -> {
            with(event.roomCreated) {
                events.onNext(RoomCreated(Room(UUID.fromString(id), name)))
            }
        }
        ROOM_NOT_CREATED -> {
            events.onNext(RoomNotCreated)
        }
        ROOM_REMOVED -> {
            events.onNext(RoomRemoved(UUID.fromString(event.roomRemoved.id)))
        }
        ROOM_JOINED -> {
            events.onNext(RoomJoined)
        }
        ROOM_NOT_JOINED -> {
            events.onNext(RoomNotJoined)
        }
        ROOM_LEAVED -> {

        }
        ROOM_NOT_LEAVED -> {

        }
        EVENT_NOT_SET, null -> { }
    }

    private fun createRoomsList(): Observable<List<Room>> {
        return events
            .scan(listOf<Room>()) { list, event ->
                return@scan when (event) {
                    is RoomsReceived -> event.rooms
                    is RoomCreated -> list + event.room
                    is RoomRemoved -> list.filter { it.id != event.roomId }
                    else -> list
                }
            }
            .replay(1)
            .autoConnect()
    }

    private fun sendIntent(builder: SignalingIntent.Builder.() -> Unit) {
        val data = SignalingIntent.newBuilder()
            .apply { builder() }
            .build().toByteArray()
        ws.send(ByteString.of(ByteBuffer.wrap(data)))
    }

}