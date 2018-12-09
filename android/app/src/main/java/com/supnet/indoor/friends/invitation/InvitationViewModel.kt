package com.supnet.indoor.friends.invitation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.common.AutoDisposableViewModel
import com.supnet.common.Command
import com.supnet.common.SchedulersProvider
import com.supnet.domain.user.UserManagerResult
import com.supnet.domain.user.UserManagerResult.InvitationResult.*
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign

class InvitationViewModel(
    private val results: Observable<UserManagerResult.InvitationResult>,
    private val onSendInvitation: (recipient: String, message: String) -> Unit,
    private val schedulersProvider: SchedulersProvider
) : AutoDisposableViewModel() {

    private val liveState = MutableLiveData<InvitationState>()
    private val liveCommands = MutableLiveData<Command<InvitationCommand>>()

    init {
        liveState.value = InvitationState(false)

        disposables += results
            .observeOn(schedulersProvider.ui())
            .subscribe {
                return@subscribe when (it) {
                    InvitationSend -> {
                        liveCommands.value = Command(InvitationCommand.DISMISS)
                    }
                    InvitationFailure -> {
                        liveCommands.value = Command(InvitationCommand.SHOW_ERROR)
                        liveState.value = InvitationState(false)
                    }
                }
            }
    }

    fun sendInvitation(recipient: String, message: String) {
        onSendInvitation(recipient, message)
        liveState.value = InvitationState(true)
    }

    fun getLiveState(): LiveData<InvitationState> = liveState

    fun getLiveCommand(): LiveData<Command<InvitationCommand>> = liveCommands

}