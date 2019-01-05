package com.supnet.indoor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.common.AutoDisposableViewModel
import com.supnet.common.Command
import com.supnet.domain.connection.ConnectionManager
import com.supnet.domain.connection.ConnectionManagerState.*
import com.supnet.indoor.IndoorCommand.*
import io.reactivex.rxkotlin.plusAssign

class IndoorViewModel(connectionManager: ConnectionManager) : AutoDisposableViewModel() {

    private val liveCommands = MutableLiveData<Command<IndoorCommand>>()

    init {

        disposables += connectionManager
            .states()
            .subscribe {
                when (it) {
                    IDLE -> {

                    }
                    CONNECTED -> {

                    }
                }
            }

    }

    fun getLiveCommands(): LiveData<Command<IndoorCommand>> = liveCommands

    fun onFriendsSelected() = postCommand(ShowFriends)

    fun onGadgetsSelected() = postCommand(ShowGadgets)

    fun onSettingsSelected() = postCommand(ShowSettings)

    private fun postCommand(cmd: IndoorCommand) {
        liveCommands.value = Command(cmd)
    }

}