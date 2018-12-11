package com.supnet.indoor.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.Supnet

class FriendsViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FriendsViewModel(
            Supnet.app.friendsManager,
            Supnet.schedulersProvider,
            FriendsReducer()
        ) as T
    }

}