package com.supnet.rooms.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.signaling.SignalingClient

class RoomsListViewModelFactory(
    private val signalingClient: SignalingClient
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RoomsListViewModel(signalingClient) as T
    }

}