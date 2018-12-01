package com.supnet.indoor.friends.invitation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.common.AutoDisposableViewModel
import io.reactivex.Completable

class InvitationViewModel(
    private val onSendInvitation: (recipient: String, message: String) -> Completable
) : AutoDisposableViewModel() {

    private val liveState = MutableLiveData<Boolean>()

    init {
        liveState.value = false
    }

    fun sendInvitation() {
        liveState.value = true
    }

    fun getLiveState(): LiveData<Boolean> = liveState

}