package com.supnet.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supnet.common.Command
import com.supnet.navigation.NavigationViewModel.NavigationCommand.*
import com.supnet.signaling.SignalingClient
import com.supnet.signaling.SignalingClient.SignalingState.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import java.util.*

class NavigationViewModel(
    private val signalingClient: SignalingClient
) : ViewModel(), RoomsListNavigator {

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
            postCommand(Command(ShowLoading))
        }
        Connected -> {
            postCommand(Command(ShowRooms))
        }
        Closed -> {
            postCommand(Command(ShowError))
        }
    }

    override fun onRoomCreated(roomId: UUID) {
        postCommand(Command(ShowRoom(roomId)))
    }

    private fun postCommand(cmd: Command<NavigationCommand>) {
        liveCommands.postValue(cmd)
    }

    override fun onCleared() {
        super.onCleared()
        signalingClient.close()
        disposables.dispose()
    }

}