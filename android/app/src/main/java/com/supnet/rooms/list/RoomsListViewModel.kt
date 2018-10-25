package com.supnet.rooms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supnet.signaling.SignalingClient
import com.supnet.signaling.SignalingClient.RoomsEvent
import com.supnet.signaling.SignalingClient.RoomsEvent.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

class RoomsViewModel(
    private val signalingClient: SignalingClient
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val liveRooms = MutableLiveData<List<String>>()

    init {
        disposables += signalingClient.getEvents()
            .subscribe(::onRoomsEvent)
    }

    fun getRooms(): LiveData<List<String>> = liveRooms

    private fun onRoomsEvent(event: RoomsEvent): Unit = when (event) {
        is RoomsReceived -> {
            liveRooms.postValue(event.rooms)
        }
        RoomCreated -> {

        }
        RoomNotCreated -> {

        }
    }

    override fun onCleared() {
    }

    fun onJoinRoom(it: String) {

    }

    fun onCreateRoom(name: String) {


    }
}