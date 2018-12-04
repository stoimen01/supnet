package com.supnet.indoor.friends.invitation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.Supnet
import com.supnet.data.SupnetIntent
import com.supnet.data.SupnetResult

class InvitationViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return InvitationViewModel(
            Supnet.supnetRepository.results().ofType(SupnetResult.InvitationResult::class.java),
            { recipient, msg -> Supnet.supnetRepository.sendIntent(SupnetIntent.InvitationIntent(recipient, msg)) },
            Supnet.schedulersProvider
        ) as T
    }

}