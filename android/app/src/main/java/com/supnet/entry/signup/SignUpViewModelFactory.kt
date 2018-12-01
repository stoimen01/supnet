package com.supnet.entry.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.Supnet
import com.supnet.data.connection.ConnectionAgent
import com.supnet.data.supnet.SupnetRepository

class SignUpViewModelFactory(
    private val connectionAgent: ConnectionAgent,
    private val supnetRepository: SupnetRepository,
    private val navigator: SignUpNavigator
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SignUpViewModel(
            connectionAgent.getConnectionStates(),
            supnetRepository.signUpEvents(),
            supnetRepository::signUp,
            navigator,
            Supnet.schedulersProvider,
            SignUpReducer()
        ) as T
    }

}