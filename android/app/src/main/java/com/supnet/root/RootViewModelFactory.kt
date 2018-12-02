package com.supnet.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.data.SupnetRepository

class RootViewModelFactory(
    private val supnetRepository: SupnetRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RootViewModel(
            supnetRepository.userStates()
        ) as T
    }

}