package com.supnet.rooms.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supnet.common.BaseViewModel
import com.supnet.signaling.rooms.RoomsManager
import com.supnet.signaling.entities.Message
import com.supnet.signaling.entities.Room
import com.supnet.signaling.entities.User
import com.supnet.signaling.rooms.RoomsManager.State.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import java.util.*

class RoomViewModel(
    private val roomId: UUID,
    private val roomsManager: RoomsManager
) : BaseViewModel() {

    private val liveRoom = MutableLiveData<Room>()
    private val liveMessages = MutableLiveData<List<Message>>()

    init {
        disposables += roomsManager.getState().subscribe(this::onState)
        liveMessages.postValue(generateFakeMessages())
    }

    fun getLiveRoom(): LiveData<Room> = liveRoom

    fun getLiveMessages(): LiveData<List<Message>> = liveMessages

    fun onLeaveRoom() = roomsManager.leaveRoom(roomId)

    private fun onState(state: RoomsManager.State) = when (state) {
        is InRoom -> {
            liveRoom.postValue(state.backState.rooms.find { it.id == roomId })
        }
        else -> { /*no-op*/ }
    }

    private fun generateFakeMessages(): List<Message> {
        return mutableListOf<Message>().apply {
            for (i in 1..100) {
                add(
                    Message(
                        User(UUID.randomUUID(), "petko$i"), """asd
                    asdasdasdasdasdaisudpasuuhdauhsuhdashuduhasuhfuhasuhduhasuhdaus
                    asuhduhasufuhasuhduhahsdashduhashdhasdauisodaosdashduasdauhsdhasu
                    """)
                )
            }
        }
    }
}