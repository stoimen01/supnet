package com.supnet.indoor.friends.invitation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InvitationViewModel : ViewModel() {

    private val liveState = MutableLiveData<Boolean>()

    init {
        liveState.value = false
    }

    fun sendInvitation() {
        liveState.value = true
    }

    fun getLiveState(): LiveData<Boolean> = liveState

}