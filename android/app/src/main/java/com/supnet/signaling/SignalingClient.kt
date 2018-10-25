package com.supnet.signaling

import com.google.protobuf.InvalidProtocolBufferException
import com.supnet.signaling.SignalingClient.RoomsEvent.*
import com.supnet.signaling.SignalingClient.SignalingState.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import okhttp3.*
import okio.ByteString
import proto.CreateRoomIntent
import proto.SignalingEvent
import proto.SignalingEvent.EventCase.*
import proto.SignalingIntent
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeUnit

class SignalingClient(
    private val client: OkHttpClient
) : WebSocketListener() {

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
        val intent = SignalingIntent.newBuilder()
            .setCreateRoom(CreateRoomIntent.newBuilder().setRoomName(name))
            .build()
            .toByteArray()

        return Observables.combineLatest(events, send(intent).toObservable<Unit>().startWith(Unit)) { event, _ -> event }
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
            val rooms = event.roomsInfo.listList.map { Room(UUID.fromString(it.id), it.name) }
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

        }
        ROOM_NOT_JOINED -> {

        }
        ROOM_LEAVED -> {

        }
        ROOM_NOT_LEAVED -> {

        }
        EVENT_NOT_SET, null -> { }
    }

    private fun createRoomsList(): Observable<List<Room>> {
        return events
            .scan(listOf()) { list, event ->
                return@scan when (event) {
                    is RoomsReceived -> event.rooms
                    is RoomCreated -> list + event.room
                    is RoomRemoved -> list.filter { it.id != event.roomId }
                    else -> list
                }
            }
    }

    private fun send(data: ByteArray): Completable {
        return if (ws.send(ByteString.of(ByteBuffer.wrap(data)))) {
            Completable.complete()
        } else {
            Completable.error(Throwable("Cannot send data !"))
        }
    }
}