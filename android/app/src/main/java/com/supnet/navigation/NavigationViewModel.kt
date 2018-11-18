package com.supnet.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.common.BaseViewModel
import com.supnet.common.Command
import com.supnet.navigation.NavigationCommand.*
import com.supnet.signaling.rooms.RoomsManager
import com.supnet.signaling.rooms.RoomsState
import com.supnet.signaling.rooms.RoomsState.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign

class NavigationViewModel(
    connect: () -> Unit,
    private val disconnect: () -> Unit,
    stateStream: Observable<RoomsState>
) : BaseViewModel() {

    private val liveCommands = MutableLiveData<Command<NavigationCommand>>()

    init {

        disposables += stateStream
            .distinctUntilChanged { t1, t2 ->
                t1.javaClass.canonicalName == t2.javaClass.canonicalName
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::onConnectionState)

        connect()
    }

    fun getCommands(): LiveData<Command<NavigationCommand>> = liveCommands

    private fun onConnectionState(state: RoomsState) = when (state) {
        Idle, Connecting, Connected -> postCommand(ShowLoading)
        Disconnecting, Disconnected -> postCommand(ShowError)
        is InLobby -> postCommand(ShowRooms)
        is CreatingRoom -> postCommand(ShowLoading)
        is JoiningRoom -> postCommand(ShowLoading)
        is InRoom -> postCommand(ShowRoom)
    }

    private fun postCommand(cmd: NavigationCommand) {
        liveCommands.postValue(Command(cmd))
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

}