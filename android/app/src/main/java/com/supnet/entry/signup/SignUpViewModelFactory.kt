package com.supnet.entry.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.Supnet
import com.supnet.data.SupnetIntent
import com.supnet.device.connection.ConnectionAgent
import com.supnet.data.SupnetRepository
import com.supnet.data.SupnetResult

class SignUpViewModelFactory(
    private val connectionAgent: ConnectionAgent,
    private val supnetRepository: SupnetRepository,
    private val navigator: SignUpNavigator
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SignUpViewModel(
            connectionAgent.getConnectionStates(),
            supnetRepository.results().ofType(SupnetResult.SignUpResult::class.java),
            { email, name, pass -> supnetRepository.sendIntent(SupnetIntent.SignUpIntent(email, name, pass))},
            navigator,
            Supnet.schedulersProvider,
            SignUpReducer()
        ) as T
    }

}