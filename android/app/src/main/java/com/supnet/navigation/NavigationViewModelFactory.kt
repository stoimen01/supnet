package com.supnet.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.signaling.rooms.RoomsManager

class NavigationViewModelFactory(
    private val roomsManager: RoomsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NavigationViewModel(
            roomsManager::connect,
            roomsManager::disconnect,
            roomsManager.getState()
        ) as T
    }

}