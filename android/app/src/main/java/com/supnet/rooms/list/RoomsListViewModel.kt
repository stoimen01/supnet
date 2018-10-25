package com.supnet.rooms.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supnet.common.Command
import com.supnet.signaling.SignalingClient
import com.supnet.signaling.SignalingClient.RoomsEvent
import com.supnet.signaling.SignalingClient.RoomsEvent.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

class RoomsListViewModel(
    private val signalingClient: SignalingClient
) : ViewModel() {

    enum class RoomsListCommand {
        SHOW_LOADING, SHOW_ROOM_CREATE_ERROR
    }

    private val disposables = CompositeDisposable()
    private val liveRooms = MutableLiveData<List<String>>()
    private val liveCommands = MutableLiveData<Command<RoomsListCommand>>()

    init {
        disposables += signalingClient.getEvents()
            .subscribe(::onRoomsEvent)
    }

    fun getLiveRooms(): LiveData<List<String>> = liveRooms

    fun getLiveCommands(): LiveData<Command<RoomsListCommand>> = liveCommands

    private fun onRoomsEvent(event: RoomsEvent): Unit = when (event) {
        is RoomsReceived -> {
            liveRooms.postValue(event.rooms)
        }
        RoomCreated -> {
            // TODO:
        }
        RoomNotCreated -> {
            liveCommands.postValue(Command(RoomsListCommand.SHOW_ROOM_CREATE_ERROR))
        }
        RoomCreating -> {
            liveCommands.postValue(Command(RoomsListCommand.SHOW_LOADING))
        }
    }

    override fun onCleared() {
    }

    fun onJoinRoom(it: String) {

    }

    fun onCreateRoom(name: String) {
        signalingClient.createRoom(name)
    }
}