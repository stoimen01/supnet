package com.supnet.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.model.connection.ConnectionAgent

class UserNavigationViewModelFactory(
    private val connectionAgent: ConnectionAgent
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserNavigationViewModel(
            connectionAgent
        ) as T
    }

}