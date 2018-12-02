package com.supnet.indoor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.common.AutoDisposableViewModel
import com.supnet.common.Command
import com.supnet.indoor.IndoorCommand.*

class IndoorViewModel : AutoDisposableViewModel() {

    private val liveCommands = MutableLiveData<Command<IndoorCommand>>()

    init {
        postCommand(ShowFriends)
    }

    fun getLiveCommands(): LiveData<Command<IndoorCommand>> = liveCommands

    fun onFriendsSelected() = postCommand(ShowFriends)

    fun onGadgetsSelected() = postCommand(ShowGadgets)

    fun onSettingsSelected() = postCommand(ShowSettings)

    private fun postCommand(cmd: IndoorCommand) {
        liveCommands.value = Command(cmd)
    }

}