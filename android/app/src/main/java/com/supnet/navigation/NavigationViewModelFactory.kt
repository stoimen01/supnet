package com.supnet.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.signaling.SignalingClient

class NavigationViewModelFactory(
    private val signalingClient: SignalingClient
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NavigationViewModel(signalingClient) as T
    }

}