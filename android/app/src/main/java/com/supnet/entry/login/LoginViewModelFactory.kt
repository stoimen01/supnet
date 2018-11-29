package com.supnet.entry.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.Supnet
import com.supnet.data.connection.ConnectionAgent
import com.supnet.data.credentials.CredentialsRepository

class LoginViewModelFactory(
    private val connectionAgent: ConnectionAgent,
    private val credentialsRepository: CredentialsRepository,
    private val navigator: LoginNavigator
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(
            connectionAgent.getConnectionStates(),
            credentialsRepository::signIn,
            navigator,
            Supnet.schedulersProvider,
            LoginReducer()
        ) as T
    }

}