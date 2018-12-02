package com.supnet.indoor.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.data.SupnetIntent
import com.supnet.data.SupnetRepository

class SettingsViewModelFactory(
    private val supnetRepository: SupnetRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsViewModel { supnetRepository.sendIntent(SupnetIntent.SignOutIntent) } as T
    }

}