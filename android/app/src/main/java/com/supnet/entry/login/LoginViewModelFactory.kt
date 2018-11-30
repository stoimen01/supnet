package com.supnet.entry.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.Supnet
import com.supnet.data.connection.ConnectionAgent
import com.supnet.data.credentials.SupnetRepository

class LoginViewModelFactory(
    private val connectionAgent: ConnectionAgent,
    private val supnetRepository: SupnetRepository,
    private val navigator: LoginNavigator
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(
            connectionAgent.getConnectionStates(),
            supnetRepository::signIn,
            navigator,
            Supnet.schedulersProvider,
            LoginReducer()
        ) as T
    }

}