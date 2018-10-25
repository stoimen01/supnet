package com.supnet.rooms.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.navigation.RoomsListNavigator
import com.supnet.signaling.SignalingClient

class RoomsListViewModelFactory(
    private val signalingClient: SignalingClient,
    private val navigator: RoomsListNavigator
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RoomsListViewModel(signalingClient, navigator) as T
    }

}