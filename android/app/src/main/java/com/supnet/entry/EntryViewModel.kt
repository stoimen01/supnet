package com.supnet.entry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.common.AutoDisposableViewModel
import com.supnet.common.Command
import com.supnet.entry.register.RegisterNavigator
import com.supnet.entry.EntryCommand.*
import com.supnet.data.connection.ConnectionState
import com.supnet.entry.login.LoginNavigator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign

class EntryViewModel(
    private val entryFlowNavigator: EntryFlowNavigator
) : AutoDisposableViewModel(), RegisterNavigator, LoginNavigator {

    private val liveCommands = MutableLiveData<Command<EntryCommand>>()

    init { postCommand(ShowLogin) }

    fun getCommands(): LiveData<Command<EntryCommand>> = liveCommands

    override fun onSuccessfulLogin() = entryFlowNavigator.onSuccessfulLogin()

    override fun onRegisterSuccessful() = entryFlowNavigator.onSuccessfulRegistration()

    override fun onBackFromRegisterClicked() = postCommand(BackFromRegister)

    override fun onCreateAccount() = postCommand(ShowRegister)

    private fun postCommand(cmd: EntryCommand) = liveCommands.postValue(Command(cmd))

}