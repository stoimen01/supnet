package com.supnet.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.domain.user.UserManager

class RootViewModelFactory(
    private val userManager: UserManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RootViewModel(
            userManager.userStates()
        ) as T
    }

}