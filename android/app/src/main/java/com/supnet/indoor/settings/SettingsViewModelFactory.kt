package com.supnet.indoor.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.data.credentials.CredentialsRepository

class SettingsViewModelFactory(
    private val credentialsRepository: CredentialsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsViewModel(credentialsRepository.signOut()) as T
    }

}