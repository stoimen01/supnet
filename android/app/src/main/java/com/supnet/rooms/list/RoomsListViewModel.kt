package com.supnet.rooms.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supnet.common.Command
import com.supnet.navigation.RoomsListNavigator
import com.supnet.signaling.Room
import com.supnet.signaling.SignalingClient
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

class RoomsListViewModel(
    private val signalingClient: SignalingClient,
    private val navigator: RoomsListNavigator
) : ViewModel() {

    enum class RoomsListCommand {
        SHOW_LOADING, SHOW_ROOM_CREATE_ERROR
    }

    private val disposables = CompositeDisposable()
    private val liveRooms = MutableLiveData<List<Room>>()
    private val liveCommands = MutableLiveData<Command<RoomsListCommand>>()

    init {
        disposables += signalingClient.getRooms()
            .subscribe {
                liveRooms.postValue(it)
            }
    }

    fun getLiveRooms(): LiveData<List<Room>> = liveRooms

    fun getLiveCommands(): LiveData<Command<RoomsListCommand>> = liveCommands

    fun onJoinRoom(it: String) {

    }

    fun onCreateRoom(name: String) {
        liveCommands.postValue(Command(RoomsListCommand.SHOW_LOADING))
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