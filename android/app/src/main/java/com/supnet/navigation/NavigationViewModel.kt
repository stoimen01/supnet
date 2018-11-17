package com.supnet.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.common.BaseViewModel
import com.supnet.common.VEvent
import com.supnet.navigation.NavigationCommand.*
import com.supnet.signaling.rooms.RoomsManager
import com.supnet.signaling.rooms.RoomsManager.State.*
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign

class NavigationViewModel(
    connect: () -> Unit,
    private val disconnect: () -> Unit,
    stateStream: Observable<RoomsManager.State>,
    stateLog: Observable<String>
) : BaseViewModel() {

    private val liveCommands = MutableLiveData<VEvent<NavigationCommand>>()

    init {

        disposables += stateStream.subscribe(::onConnectionState)

        disposables += stateLog.subscribe { postCommand(LogMessage(it)) }

        connect()
    }

    fun getCommands(): LiveData<VEvent<NavigationCommand>> = liveCommands

    private fun onConnectionState(state: RoomsManager.State) = when (state) {
        Idle, Connecting, Connected -> postCommand(ShowLoading)
        Disconnecting, Disconnected -> postCommand(ShowError)
        is InLobby -> postCommand(ShowRooms)
        is CreatingRoom -> postCommand(ShowLoading)
        is JoiningRoom -> postCommand(ShowLoading)
        is InRoom -> postCommand(ShowRoom)
    }

    private fun postCommand(cmd: NavigationCommand) {
        liveCommands.postValue(VEvent(cmd))
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

}