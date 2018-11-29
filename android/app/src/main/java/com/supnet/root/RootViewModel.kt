package com.supnet.root

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.common.AutoDisposableViewModel
import com.supnet.common.Command
import com.supnet.root.RootCommand.*
import com.supnet.entry.EntryFlowNavigator

class RootViewModel : AutoDisposableViewModel(), EntryFlowNavigator {

    private val liveCommands = MutableLiveData<Command<RootCommand>>()

    init {
        postCommand(ShowEntryFlow)
    }

    override fun onSuccessfulRegistration() = postCommand(ShowIndoorFlow)

    override fun onSuccessfulLogin() = postCommand(ShowIndoorFlow)

    private fun postCommand(cmd: RootCommand) {
        liveCommands.value = Command(cmd)
    }

    fun getCommands(): LiveData<Command<RootCommand>> = liveCommands
}