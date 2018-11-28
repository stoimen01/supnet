package com.supnet.entry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.common.BaseViewModel
import com.supnet.common.Command
import com.supnet.entry.login.LoginViewModel
import com.supnet.entry.register.RegisterViewModel
import com.supnet.entry.EntryCommand.*
import com.supnet.model.connection.ConnectionState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign

class EntryViewModel(
    private val connectionStates: Observable<ConnectionState>,
    private val onRegister: (email: String, username: String, password: String) -> Unit,
    private val onLogin: (email: String, password: String) -> Unit
) : BaseViewModel(), RegisterViewModel, LoginViewModel {

    private val liveCommands = MutableLiveData<Command<EntryCommand>>()

    private val liveConnectionState = MutableLiveData<ConnectionState>()

    init {

        disposables += connectionStates
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                liveConnectionState.value = it
            }

        postCommand(ShowLogin)
    }

    fun getCommands(): LiveData<Command<EntryCommand>> = liveCommands

    override fun getConnectionState(): LiveData<ConnectionState> = liveConnectionState

    override fun onRegisterClicked(
        email: String,
        username: String,
        password: String
    ) = onRegister(email, username, password)

    override fun onLoginClicked(
        email: String,
        password: String
    ) = onLogin(email, password)

    override fun onBackFromRegisterClicked() {
        postCommand(BackFromRegister)
    }

    override fun onCreateAccountClicked() {
        postCommand(ShowRegister)
    }

    private fun postCommand(cmd: EntryCommand) {
        liveCommands.postValue(Command(cmd))
    }

}