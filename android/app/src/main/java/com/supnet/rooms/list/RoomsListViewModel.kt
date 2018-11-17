package com.supnet.rooms.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.common.BaseViewModel
import com.supnet.common.Command
import com.supnet.rooms.list.RoomsListViewModel.RoomsListState.*
import com.supnet.signaling.entities.Room
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import java.util.*

class RoomsListViewModel(
    rooms: Observable<List<Room>>,
    private val joinRoom: (UUID) -> Unit,
    private val createRoom: (String) -> Unit
) : BaseViewModel() {

    enum class RoomsListCommand {
        SHOW_ROOM_CREATE_ERROR,
        SHOW_ROOM_JOIN_ERROR
    }

    sealed class RoomsListState {
        object Loading : RoomsListState()
        object Empty : RoomsListState()
        data class Available(val rooms: List<Room>) : RoomsListState()
    }

    private val liveState = MutableLiveData<RoomsListState>()
    private val liveCommands = MutableLiveData<Command<RoomsListCommand>>()

    init {
        disposables += rooms
            .subscribe ({
                if (it.isEmpty()) {
                    liveState.postValue(Empty)
                } else {
                    liveState.postValue(Available(it))
                }
            }, {

            })
    }

    fun getLiveState(): LiveData<RoomsListState> = liveState

    fun getLiveCommands(): LiveData<Command<RoomsListCommand>> = liveCommands

    fun onJoinRoom(roomId: UUID) = joinRoom(roomId)

    fun onCreateRoom(name: String) = createRoom(name)

}