package com.supnet.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.model.connection.ConnectionAgent
import com.supnet.model.credentials.CredentialsManager

class EntryViewModelFactory(
    private val connectionAgent: ConnectionAgent,
    private val credentialsManager: CredentialsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EntryViewModel(
            connectionAgent.getConnectionStates(),
            credentialsManager::registerUser,
            credentialsManager::loginUser
        ) as T
    }

}