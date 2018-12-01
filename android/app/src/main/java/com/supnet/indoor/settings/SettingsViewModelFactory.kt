package com.supnet.indoor.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.data.supnet.SupnetRepository

class SettingsViewModelFactory(
    private val supnetRepository: SupnetRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsViewModel(supnetRepository.signOut()) as T
    }

}