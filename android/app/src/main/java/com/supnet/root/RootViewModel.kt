package com.supnet.root

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.data.UserState
import com.supnet.data.UserState.*
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
                return@subscribe when (it!!) {
                    SignedOut -> postCommand(ShowEntryFlow)
                    is SignedIn -> postCommand(ShowIndoorFlow)
                    Undefined, Loading -> { /*no-op*/ }
                }
            }
    }

    private fun postCommand(cmd: RootCommand) {
        liveCommands.postValue(Command(cmd))
    }

    fun getCommands(): LiveData<Command<RootCommand>> = liveCommands
}