package com.supnet.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.signaling.SignalingClient

class RoomsViewModelFactory(
    private val signalingClient: SignalingClient
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RoomsViewModel(signalingClient) as T
    }

}