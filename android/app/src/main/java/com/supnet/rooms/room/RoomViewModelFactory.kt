package com.supnet.rooms.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.signaling.SignalingClient
import java.util.*

class RoomViewModelFactory(
    private val roomId: UUID,
    private val signalingClient: SignalingClient,
    private val navigator: RoomNavigator
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RoomViewModel(roomId, signalingClient, navigator) as T
    }

}