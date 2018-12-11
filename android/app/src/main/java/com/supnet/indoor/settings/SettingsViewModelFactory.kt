package com.supnet.indoor.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.Supnet
import com.supnet.domain.user.UserManagerIntent
import com.supnet.domain.user.UserManagerResult

class SettingsViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsViewModel(
            Supnet.app.connectionAgent.getConnectionStates(),
            Supnet.app.userManager.results().ofType(UserManagerResult.SignOutResult::class.java),
            Supnet.app.userManager.results().ofType(UserManagerResult.SignOffResult::class.java),
            { Supnet.app.userManager.sendIntent(UserManagerIntent.SignOutIntent) },
            { Supnet.app.userManager.sendIntent(UserManagerIntent.SignOffIntent) },
            Supnet.schedulersProvider
        ) as T
    }

}