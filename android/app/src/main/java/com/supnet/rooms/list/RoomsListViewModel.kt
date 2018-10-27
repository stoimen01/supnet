package com.supnet.rooms.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supnet.common.BaseViewModel
import com.supnet.common.Command
import com.supnet.rooms.list.RoomsListViewModel.RoomsListState.*
import com.supnet.signaling.rooms.RoomsManager
import com.supnet.signaling.entities.Room
import com.supnet.signaling.rooms.RoomsManager.State.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import java.util.*

class RoomsListViewModel(
    private val roomsManager: RoomsManager
) : BaseViewModel() {

    enum class RoomsListCommand {
        SHOW_ROOM_CREATE_ERROR, SHOW_ROOM_JOIN_ERROR
    }

    sealed class RoomsListState {
        object Loading : RoomsListState()
        object Empty : RoomsListState()
        data class Available(val rooms: List<Room>) : RoomsListState()
    }

    private val liveState = MutableLiveData<RoomsListState>()
    private val liveCommands = MutableLiveData<Command<RoomsListCommand>>()

    init { disposables += roomsManager.getState().subscribe(this::onState) }

    fun getLiveState(): LiveData<RoomsListState> = liveState

    fun getLiveCommands(): LiveData<Command<RoomsListCommand>> = liveCommands

    fun onJoinRoom(roomId: UUID) = roomsManager.joinRoom(roomId)

    fun onCreateRoom(name: String) = roomsManager.createRoom(name)

    private fun onState(state: RoomsManager.State) = when (state) {
        is InLobby -> {
            if (state.rooms.isEmpty()) {
                liveState.postValue(Empty)
            } else {
                liveState.postValue(Available(state.rooms))
            }
        }
        else -> { /* no-op */ }
    }

}