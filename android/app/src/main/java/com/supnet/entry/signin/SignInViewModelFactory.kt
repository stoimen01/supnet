package com.supnet.entry.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.Supnet
import com.supnet.data.connection.ConnectionAgent
import com.supnet.data.supnet.SupnetRepository

class SignInViewModelFactory(
    private val connectionAgent: ConnectionAgent,
    private val supnetRepository: SupnetRepository,
    private val navigator: SignInNavigator
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SignInViewModel(
            connectionAgent.getConnectionStates(),
            supnetRepository.signInEvents(),
            supnetRepository::signIn,
            navigator,
            Supnet.schedulersProvider,
            SignInReducer()
        ) as T
    }

}