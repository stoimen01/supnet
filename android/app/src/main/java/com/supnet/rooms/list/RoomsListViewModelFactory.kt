package com.supnet.rooms.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.signaling.rooms.RoomsManager

class RoomsListViewModelFactory(
    private val roomsManager: RoomsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RoomsListViewModel(roomsManager) as T
    }

}