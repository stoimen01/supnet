package com.supnet.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supnet.common.BaseViewModel
import com.supnet.common.Command
import com.supnet.navigation.NavigationViewModel.NavigationCommand.*
import com.supnet.rooms.list.RoomsListNavigator
import com.supnet.rooms.room.RoomNavigator
import com.supnet.signaling.rooms.RoomsManager
import com.supnet.signaling.rooms.RoomsManager.State.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import java.util.*

class NavigationViewModel(
    private val roomsManager: RoomsManager
) : BaseViewModel(), RoomsListNavigator, RoomNavigator {

    sealed class NavigationCommand {
        object ShowLoading : NavigationCommand()
        object ShowRooms : NavigationCommand()
        object ShowError : NavigationCommand()
        data class ShowRoom(val roomId: UUID) : NavigationCommand()
        data class LogMessage(val data: String): NavigationCommand()
    }

    private val liveCommands = MutableLiveData<Command<NavigationCommand>>()

    init {

        disposables += roomsManager.getState()
            .subscribe(::onConnectionState)

        disposables += roomsManager.getStateLog()
            .subscribe {
                postCommand(LogMessage(it))
            }

        roomsManager.connect()
    }

    fun getCommands(): LiveData<Command<NavigationCommand>> = liveCommands

    private fun onConnectionState(state: RoomsManager.State) = when (state) {
        Idle, Connecting, Connected -> postCommand(ShowLoading)
        Disconnecting, Disconnected -> postCommand(ShowError)
        is InLobby -> postCommand(ShowRooms)
        is CreatingRoom -> postCommand(ShowLoading)
        is JoiningRoom -> postCommand(ShowLoading)
        is InRoom -> postCommand(ShowRoom(state.roomId))
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
        roomsManager.disconnect()
    }

}