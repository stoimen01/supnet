package com.supnet.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supnet.common.Command
import com.supnet.navigation.NavigationViewModel.NavigationCommand.*
import com.supnet.rooms.list.RoomsListNavigator
import com.supnet.rooms.room.RoomNavigator
import com.supnet.signaling.SignalingClient
import com.supnet.signaling.SignalingClient.SignalingState.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import java.util.*

class NavigationViewModel(
    private val signalingClient: SignalingClient
) : ViewModel(), RoomsListNavigator, RoomNavigator {

    sealed class NavigationCommand {
        object ShowLoading : NavigationCommand()
        object ShowRooms : NavigationCommand()
        object ShowError : NavigationCommand()
        data class ShowRoom(val roomId: UUID) : NavigationCommand()
    }

    private val liveCommands = MutableLiveData<Command<NavigationCommand>>()
    private val disposables = CompositeDisposable()

    init {
        signalingClient.connect()
        disposables += signalingClient.getStates()
            .subscribe(::onSignalingState)
    }

    fun getCommands(): LiveData<Command<NavigationCommand>> = liveCommands

    private fun onSignalingState(state: SignalingClient.SignalingState) = when (state) {
        Idle -> { }
        Connecting -> {
            postCommand(ShowLoading)
        }
        Connected -> {
            postCommand(ShowRooms)
        }
        Closed -> {
            postCommand(ShowError)
        }
    }

    override fun onRoomCreated(roomId: UUID) {
        postCommand(ShowRoom(roomId))
    }

    override fun onRoomJoined(roomId: UUID) {
        postCommand(ShowRoom(roomId))
    }

    override fun onRoomExitClicked() {
        postCommand(ShowRooms)
    }

    private fun postCommand(cmd: NavigationCommand) {
        liveCommands.postValue(Command(cmd))
    }

    override fun onCleared() {
        super.onCleared()
        signalingClient.close()
        disposables.dispose()
    }

}