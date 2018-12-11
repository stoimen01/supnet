package com.supnet.indoor.friends.invitation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.Supnet
import com.supnet.domain.friends.FriendsManagerIntent
import com.supnet.domain.friends.FriendsManagerIntent.*
import com.supnet.domain.friends.FriendsManagerResult
import com.supnet.domain.friends.FriendsManagerResult.*
import com.supnet.domain.user.UserManagerIntent
import com.supnet.domain.user.UserManagerResult

class InvitationViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return InvitationViewModel(
            Supnet.app.friendsManager.results().ofType(InvitationResult::class.java),
            { recipient, msg ->
                Supnet.app.friendsManager.sendIntent(InvitationIntent(recipient, msg))
            },
            Supnet.schedulersProvider
        ) as T
    }

}