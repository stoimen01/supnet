package com.supnet.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.common.BaseViewModel
import com.supnet.common.Command
import com.supnet.navigation.NavigationCommand.*
import com.supnet.signaling.rooms.RoomsState
import com.supnet.signaling.rooms.RoomsState.*
import com.supnet.model.credentials.CredentialsState
import com.supnet.model.credentials.CredentialsState.LoggedIn
import com.supnet.model.credentials.CredentialsState.LoggedOut
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign

class NavigationViewModel(
    credentialsStates: Observable<CredentialsState>
) : BaseViewModel() {

    private val liveCommands = MutableLiveData<Command<NavigationCommand>>()

    init {
        disposables += credentialsStates
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onLoginState)
    }

    fun getCommands(): LiveData<Command<NavigationCommand>> = liveCommands

    private fun onLoginState(state: CredentialsState) = when (state) {
        is LoggedIn -> postCommand(ShowRooms)
        LoggedOut -> postCommand(ShowLogin)
    }

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

}