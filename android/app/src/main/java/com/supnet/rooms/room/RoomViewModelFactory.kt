package com.supnet.rooms.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.signaling.rooms.RoomsManager
import java.util.*

class RoomViewModelFactory(
    private val roomId: UUID,
    private val roomsManager: RoomsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RoomViewModel(roomId, roomsManager) as T
    }

}