package com.supnet.root

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.UserState
import com.supnet.UserState.*
import com.supnet.common.AutoDisposableViewModel
import com.supnet.common.Command
import com.supnet.root.RootCommand.*
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign

class RootViewModel(
    userStates: Observable<UserState>
) : AutoDisposableViewModel() {

    private val liveCommands = MutableLiveData<Command<RootCommand>>()

    init {
        disposables += userStates
            .subscribe {
                when (it!!) {
                    SIGNED_IN -> postCommand(ShowIndoorFlow)
                    SIGNED_OUT -> postCommand(ShowEntryFlow)
                    UNDEFINED, LOADING -> { /*no-op*/ }
                }
            }
    }

    private fun postCommand(cmd: RootCommand) {
        liveCommands.value = Command(cmd)
    }

    fun getCommands(): LiveData<Command<RootCommand>> = liveCommands
}