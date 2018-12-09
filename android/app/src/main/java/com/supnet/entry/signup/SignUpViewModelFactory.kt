package com.supnet.entry.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.Supnet
import com.supnet.domain.user.UserManagerIntent
import com.supnet.device.connection.ConnectionAgent
import com.supnet.domain.user.UserManager
import com.supnet.domain.user.UserManagerResult

class SignUpViewModelFactory(
    private val connectionAgent: ConnectionAgent,
    private val userManager: UserManager,
    private val navigator: SignUpNavigator
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SignUpViewModel(
            connectionAgent.getConnectionStates(),
            userManager.results().ofType(UserManagerResult.SignUpResult::class.java),
            { email, name, pass -> userManager.sendIntent(UserManagerIntent.SignUpIntent(email, name, pass))},
            navigator,
            Supnet.schedulersProvider,
            SignUpReducer()
        ) as T
    }

}