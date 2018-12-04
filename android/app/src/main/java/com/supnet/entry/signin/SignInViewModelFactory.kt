package com.supnet.entry.signin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.Supnet
import com.supnet.data.SupnetIntent
import com.supnet.device.connection.ConnectionAgent
import com.supnet.data.SupnetRepository
import com.supnet.data.SupnetResult

class SignInViewModelFactory(
    private val connectionAgent: ConnectionAgent,
    private val supnetRepository: SupnetRepository,
    private val navigator: SignInNavigator
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SignInViewModel(
            connectionAgent.getConnectionStates(),
            supnetRepository.results().ofType(SupnetResult.SignInResult::class.java),
            { email, pass -> supnetRepository.sendIntent(SupnetIntent.SignInIntent(email, pass)) },
            navigator,
            Supnet.schedulersProvider,
            SignInReducer()
        ) as T
    }

}