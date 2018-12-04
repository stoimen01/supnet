package com.supnet.indoor.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.Supnet
import com.supnet.data.SupnetIntent
import com.supnet.data.SupnetRepository
import com.supnet.data.SupnetResult

class SettingsViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsViewModel(
            Supnet.app.connectionAgent.getConnectionStates(),
            Supnet.supnetRepository.results().ofType(SupnetResult.SignOutResult::class.java),
            Supnet.supnetRepository.results().ofType(SupnetResult.SignOffResult::class.java),
            { Supnet.supnetRepository.sendIntent(SupnetIntent.SignOutIntent) },
            { Supnet.supnetRepository.sendIntent(SupnetIntent.SignOffIntent) },
            Supnet.schedulersProvider
        ) as T
    }

}