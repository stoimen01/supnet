package com.supnet.root

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.common.BaseViewModel
import com.supnet.common.Command
import com.supnet.root.RootCommand.*
import com.supnet.model.credentials.CredentialsState
import com.supnet.model.credentials.CredentialsState.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign

class RootViewModel(
    credentialsStates: Observable<CredentialsState>,
    errorMessages: Observable<String>
) : BaseViewModel() {

    private val liveCommands = MutableLiveData<Command<RootCommand>>()

    private val liveState = MutableLiveData<RootViewState>()

    init {
        disposables += credentialsStates
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onLoginState)

        disposables += errorMessages
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                postCommand(ShowErrorMessage(it))
            }

        liveState.value = RootViewState(false)
    }

    private fun onLoginState(state: CredentialsState) = when (state) {
        is LoggedIn -> {
            postCommand(ShowIndoorFlow)
            liveState.value = RootViewState(false)
        }
        LoggedOut -> {
            postCommand(ShowEntryFlow)
            liveState.value = RootViewState(false)
        }
        Loading -> {
            liveState.value = RootViewState(true)
        }
        Unknown -> {}
    }

    private fun postCommand(cmd: RootCommand) {
        liveCommands.value = Command(cmd)
    }

    fun getCommands(): LiveData<Command<RootCommand>> = liveCommands

    fun getLiveState(): LiveData<RootViewState> = liveState
}