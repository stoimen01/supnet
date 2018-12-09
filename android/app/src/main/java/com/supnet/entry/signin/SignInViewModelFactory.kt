package com.supnet.entry.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.Supnet
import com.supnet.domain.user.UserManagerIntent
import com.supnet.device.connection.ConnectionAgent
import com.supnet.domain.user.UserManager
import com.supnet.domain.user.UserManagerResult

class SignInViewModelFactory(
    private val connectionAgent: ConnectionAgent,
    private val userManager: UserManager,
    private val navigator: SignInNavigator
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SignInViewModel(
            connectionAgent.getConnectionStates(),
            userManager.results().ofType(UserManagerResult.SignInResult::class.java),
            { email, pass -> userManager.sendIntent(UserManagerIntent.SignInIntent(email, pass)) },
            navigator,
            Supnet.schedulersProvider,
            SignInReducer()
        ) as T
    }

}