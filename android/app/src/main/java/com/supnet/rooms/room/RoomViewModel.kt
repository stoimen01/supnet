package com.supnet.rooms.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supnet.signaling.entities.Message
import com.supnet.signaling.entities.Room
import com.supnet.signaling.client.RxSignalingClient
import com.supnet.signaling.entities.User
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import java.util.*

class RoomViewModel(
    private val roomId: UUID,
    private val signalingClient: RxSignalingClient,
    private val navigator: RoomNavigator
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val liveRoom = MutableLiveData<Room>()
    private val liveMessages = MutableLiveData<List<Message>>()

    init {

        disposables += signalingClient
            .getRoom(roomId)
            .subscribe {
                liveRoom.postValue(it)
            }

        liveMessages.postValue(generateFakeMessages())

    }

    fun getLiveRoom(): LiveData<Room> = liveRoom

    fun getLiveMessages(): LiveData<List<Message>> = liveMessages

    override fun onCleared() {
        disposables.clear()
    }

    private fun generateFakeMessages(): List<Message> {
        return mutableListOf<Message>().apply {
            for (i in 1..100) {
                add(
                    Message(
                        User(UUID.randomUUID(), "petko$i"), """asd
                    asdasdasdasdasdaisudpasuuhdauhsuhdashuduhasuhfuhasuhduhasuhdaus
                    asuhduhasufuhasuhduhahsdashduhashdhasdauisodaosdashduasdauhsdhasu
                """
                    )
                )
            }
        }
    }

    fun onLeaveRoom() {

        signalingClient.leaveRoom(roomId)


        navigator.onRoomExitClicked()
    }

}