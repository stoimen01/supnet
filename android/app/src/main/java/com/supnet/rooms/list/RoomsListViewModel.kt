package com.supnet.rooms.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supnet.common.Command
import com.supnet.rooms.list.RoomsListViewModel.RoomsListState.*
import com.supnet.signaling.entities.Room
import com.supnet.signaling.client.RxSignalingClient
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import java.util.*

class RoomsListViewModel(
    private val signalingClient: RxSignalingClient,
    private val navigator: RoomsListNavigator
) : ViewModel() {

    enum class RoomsListCommand {
        SHOW_ROOM_CREATE_ERROR, SHOW_ROOM_JOIN_ERROR
    }

    sealed class RoomsListState {
        object Loading : RoomsListState()
        object Empty : RoomsListState()
        data class Available(val rooms: List<Room>) : RoomsListState()
    }

    private val disposables = CompositeDisposable()
    private val liveState = MutableLiveData<RoomsListState>()
    private val liveCommands = MutableLiveData<Command<RoomsListCommand>>()

    init {
        liveState.postValue(Loading)
        disposables += signalingClient.getRooms()
            .subscribe {
                if (it.isEmpty()) liveState.postValue(Empty)
                else liveState.postValue(Available(it))
            }
    }

    fun getLiveState(): LiveData<RoomsListState> = liveState

    fun getLiveCommands(): LiveData<Command<RoomsListCommand>> = liveCommands

    fun onJoinRoom(roomId: UUID) {
        liveState.postValue(Loading)
        disposables += signalingClient
            .joinRoom(roomId)
            .subscribe({
                navigator.onRoomJoined(roomId)
            }, {
                liveCommands.postValue(Command(RoomsListCommand.SHOW_ROOM_JOIN_ERROR))
            })
    }

    fun onCreateRoom(name: String) {
        liveState.postValue(Loading)
        disposables += signalingClient
            .createRoom(name)
            .subscribe({
                navigator.onRoomCreated(it)
            }, {
                liveCommands.postValue(Command(RoomsListCommand.SHOW_ROOM_CREATE_ERROR))
            })
    }

    override fun onCleared() {
        disposables.clear()
    }

}