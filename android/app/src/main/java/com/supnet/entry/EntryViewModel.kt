package com.supnet.entry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.common.AutoDisposableViewModel
import com.supnet.common.Command
import com.supnet.entry.signup.SignUpNavigator
import com.supnet.entry.EntryCommand.*
import com.supnet.entry.signin.SignInNavigator

class EntryViewModel : AutoDisposableViewModel(), SignUpNavigator, SignInNavigator {

    private val liveCommands = MutableLiveData<Command<EntryCommand>>()

    init { postCommand(ShowSignIn) }

    fun getCommands(): LiveData<Command<EntryCommand>> = liveCommands

    override fun onBackFromRegisterClicked() = postCommand(ShowBack)

    override fun onSignUp() = postCommand(ShowSignUp)

    private fun postCommand(cmd: EntryCommand) = liveCommands.postValue(Command(cmd))

}