package com.supnet.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.model.credentials.CredentialsManager

class NavigationViewModelFactory(
    private val credentialsManager: CredentialsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NavigationViewModel(
            credentialsManager.getCredentialsStates()
        ) as T
    }

}