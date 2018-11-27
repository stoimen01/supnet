package com.supnet.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supnet.common.Command
import com.supnet.model.connection.ConnectionAgent
import com.supnet.user.login.LoginViewModel
import com.supnet.user.register.RegisterViewModel

class UserNavigationViewModel(
    connectionAgent: ConnectionAgent
) : ViewModel(), RegisterViewModel, LoginViewModel {

    private val liveCommands = MutableLiveData<Command<UserNavCommand>>()

    sealed class UserNavCommand {
        object ShowLogin : UserNavCommand()
        object ShowRegister : UserNavCommand()
        object BackFromRegister: UserNavCommand()
    }

    init {
        postCommand(UserNavCommand.ShowLogin)
    }

    fun getCommands(): LiveData<Command<UserNavCommand>> = liveCommands

    override fun onRegisterClicked(email: String, username: String, password: String) {

    }

    override fun onLoginClicked(name: String, password: String) {

    }

    override fun onBackFromRegisterClicked() {
        postCommand(UserNavCommand.BackFromRegister)
    }

    override fun onCreateAccountClicked() {
        postCommand(UserNavCommand.ShowRegister)
    }

    private fun postCommand(cmd: UserNavCommand) {
        liveCommands.postValue(Command(cmd))
    }

}