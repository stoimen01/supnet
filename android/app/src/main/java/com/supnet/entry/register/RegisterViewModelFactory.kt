package com.supnet.entry.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.Supnet
import com.supnet.data.connection.ConnectionAgent
import com.supnet.data.credentials.CredentialsRepository

class RegisterViewModelFactory(
    private val connectionAgent: ConnectionAgent,
    private val credentialsRepository: CredentialsRepository,
    private val navigator: RegisterNavigator
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegisterViewModel(
            connectionAgent.getConnectionStates(),
            credentialsRepository::signUp,
            navigator,
            Supnet.schedulersProvider,
            RegisterReducer()
        ) as T
    }

}