package com.supnet.rooms.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.common.AutoDisposableViewModel
import com.supnet.signaling.entities.Message
import com.supnet.signaling.entities.Room
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign

class RoomViewModel(
    roomData: Observable<Pair<Room, Set<Message>>>,
    private val sendMsg: (String) -> Unit,
    private val leaveRoom: () -> Unit
) : AutoDisposableViewModel() {

    private val liveRoomData = MutableLiveData<Pair<Room, Set<Message>>>()

    init {
        disposables += roomData.subscribe ({
            liveRoomData.postValue(it)
        }, {
            // TODO: fix this
        })
    }

    fun getLiveRoomData(): LiveData<Pair<Room, Set<Message>>> = liveRoomData

    fun onLeaveRoom() = leaveRoom()

    fun sendMessage(msg: String) = sendMsg(msg)
}