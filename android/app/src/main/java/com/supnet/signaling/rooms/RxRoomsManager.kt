package com.supnet.signaling.rooms

import com.supnet.signaling.client.SignalingClient
import com.supnet.signaling.entities.Message
import com.supnet.signaling.entities.Room
import com.supnet.signaling.rooms.RoomsEvent.*
import com.supnet.signaling.rooms.RoomsEvent.UIEvent.*
import com.supnet.signaling.rooms.RoomsState.*
import com.supnet.signaling.rooms.RoomsEffect.SignalingEffect
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.lang.IllegalStateException
import java.util.*

class RxRoomsManager(
    private val signalingClient: SignalingClient,
    private val reducer: RoomsReducer
) : RoomsManager {

    private val uiEvents = PublishSubject.create<UIEvent>()

    private val stateStream: Observable<RoomsState> by lazy {
        val bs = BehaviorSubject.create<RoomsState>()
        Observable.merge(uiEvents, signalingClient.getEvents())
            .scan(ReduceResult<RoomsState, RoomsEffect>(Idle), reducer::reduce)
            .doOnNext {
                it.effects?.forEach { effect ->
                    return@forEach when (effect) {
                        is SignalingEffect -> signalingClient.handleEffect(effect)
                    }
                }
            }
            .map { it.state }
            .distinctUntilChanged()
            .subscribe(bs)
        return@lazy bs
    }

    override fun connect() = uiEvents.onNext(OnConnect)

    override fun disconnect() = uiEvents.onNext(OnDisconnect)

    override fun createRoom(name: String) = uiEvents.onNext(OnCreateRoom(name))

    override fun joinRoom(roomId: UUID) = uiEvents.onNext(OnJoinRoom(roomId))

    override fun leaveRoom() = uiEvents.onNext(OnLeaveRoom)

    override fun sendMessage(msg: String) = uiEvents.onNext(OnSendMessage(msg))

    override fun getState() = stateStream

    override fun getRoomData(): Observable<Pair<Room, Set<Message>>> {
        return stateStream
            .flatMap { state ->
                return@flatMap when(state) {
                    is InRoom -> Observable.just(Pair(state.room, state.messages))
                    else -> Observable.error(IllegalStateException("Cannot provide room when not in room."))
                }
            }
    }

    override fun getRooms(): Observable<List<Room>> {
        return stateStream
            .flatMap { state ->
                return@flatMap when(state) {
                    is InLobby -> Observable.just(state.rooms)
                    else -> Observable.error(IllegalStateException("Cannot provide rooms when not in lobby."))
                }
            }
    }
}