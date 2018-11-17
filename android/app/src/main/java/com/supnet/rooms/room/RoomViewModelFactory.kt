package com.supnet.rooms.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.signaling.rooms.RoomsManager

class RoomViewModelFactory(
    private val roomsManager: RoomsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RoomViewModel(
            roomsManager.getRoomData(),
            roomsManager::sendMessage,
            roomsManager::leaveRoom
        ) as T
    }

}