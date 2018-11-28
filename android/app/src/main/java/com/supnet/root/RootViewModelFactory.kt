package com.supnet.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.model.credentials.CredentialsManager

class RootViewModelFactory(
    private val credentialsManager: CredentialsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RootViewModel(
            credentialsManager.getCredentialsStates(),
            credentialsManager.getErrorMessages()
        ) as T
    }

}