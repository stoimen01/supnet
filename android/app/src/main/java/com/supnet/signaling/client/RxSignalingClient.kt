package com.supnet.signaling.client

import com.google.protobuf.InvalidProtocolBufferException
import com.supnet.signaling.entities.Room
import com.supnet.signaling.rooms.RoomsEvent.SignalingEvent
import com.supnet.signaling.rooms.RoomsEvent.SignalingEvent.*
import com.supnet.signaling.rooms.RoomsEffect.SignalingEffect
import com.supnet.signaling.rooms.RoomsEffect.SignalingEffect.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import okhttp3.*
import okio.ByteString
import proto.CreateRoomIntent
import proto.JoinRoomIntent
import proto.LeaveRoomIntent
import proto.SendMessageIntent
import proto.SignalingEvent as ProtoSignalingEvent
import proto.SignalingIntent as ProtoSignalingIntent
import proto.SignalingEvent.EventCase.*
import java.nio.ByteBuffer
import java.util.*

class RxSignalingClient(
    private val client: OkHttpClient
) : WebSocketListener(), SignalingClient {

    private val events = PublishSubject.create<SignalingEvent>()

    private lateinit var ws: WebSocket

    /* SignalingClient implementation */
    override fun getEvents(): Observable<SignalingEvent> = events

    override fun handleEffect(intent: SignalingEffect) = when (intent) {
        Connect -> {
            val request = Request.Builder()
                .url("ws://10.0.2.2:8080/signaling")
                .build()
            ws = client.newWebSocket(request, this)
        }
        is CreateRoom -> {
            sendIntent { setCreateRoom(CreateRoomIntent.newBuilder().setRoomName(intent.name)) }
        }
        is JoinRoom -> {
            sendIntent { setJoinRoom(JoinRoomIntent.newBuilder().setRoomId(intent.roomId.toString())) }
        }
        is SendMessage ->  {
            sendIntent { setSendMessage(SendMessageIntent.newBuilder().setData(intent.data)) }
        }
        is LeaveRoom -> {
            sendIntent { setLeaveRoom(LeaveRoomIntent.newBuilder().setRoomId(intent.roomId.toString())) }
        }
        Disconnect -> {
            ws.close(0, "")
            Unit
        }

    }

    private fun sendIntent(builder: ProtoSignalingIntent.Builder.() -> Unit) {
        val data = ProtoSignalingIntent.newBuilder()
            .apply { builder() }
            .build().toByteArray()
        ws.send(ByteString.of(ByteBuffer.wrap(data)))
    }

    /* Websocket listener implementation */
    override fun onOpen(webSocket: WebSocket, response: Response) = events.onNext(SocketConnected)

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) = events.onNext(SocketDisconnected)

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        events.onNext(SocketFailed)
        t.printStackTrace()
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) = try {
        processEvent(ProtoSignalingEvent.parseFrom(bytes.toByteArray()))
    } catch (ex : InvalidProtocolBufferException) {
        ex.printStackTrace()
    }

    private fun processEvent(event: ProtoSignalingEvent) = when (event.eventCase) {

        ROOMS_INFO -> {
            val rooms = event.roomsInfo.listList.map {
                Room(UUID.fromString(it.id), it.name)
            }
            events.onNext(RoomsReceived(rooms))
        }

        ROOM_ADDED -> with(event.roomAdded) {
            events.onNext(RoomAdded(Room(UUID.fromString(id), name)))
        }

        ROOM_REMOVED -> events.onNext(RoomRemoved(UUID.fromString(event.roomRemoved.id)))

        ROOM_CREATED -> with(event.roomCreated) {
            events.onNext(RoomCreated(Room(UUID.fromString(id), name)))
        }

        ROOM_NOT_CREATED -> events.onNext(RoomNotCreated)

        ROOM_JOINED -> events.onNext(RoomJoined)

        ROOM_NOT_JOINED -> events.onNext(RoomNotJoined)

        ROOM_LEFT -> events.onNext(RoomLeft)

        ROOM_NOT_LEFT -> events.onNext(RoomNotLeft)

        USER_JOINED -> events.onNext(UserJoined(event.userJoined.id))

        USER_LEFT -> events.onNext(UserLeft(event.userLeft.id))

        MESSAGE_RECEIVED -> {
            val sender = event.messageReceived.senderName
            val data = event.messageReceived.data
            events.onNext(MessageReceived(sender, data))
        }

        MESSAGE_SEND -> events.onNext(MessageSend)

        MESSAGE_NOT_SEND -> events.onNext(MessageNotSend)

        EVENT_NOT_SET, null -> { }
    }
}