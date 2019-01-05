package com.supnet.indoor.friends

import com.supnet.common.BaseViewModel
import com.supnet.common.SchedulersProvider
import com.supnet.common.StateReducer
import com.supnet.domain.connection.ConnectionManager
import com.supnet.domain.connection.ConnectionManagerIntent
import com.supnet.domain.connection.ConnectionManagerIntent.*
import com.supnet.domain.friends.FriendsManager
import com.supnet.indoor.friends.FriendsEffect.*
import com.supnet.indoor.friends.FriendsEvent.*
import io.reactivex.rxkotlin.plusAssign

class FriendsViewModel(
    private val friendsManager: FriendsManager,
    private val connectionManager: ConnectionManager,
    schedulersProvider: SchedulersProvider,
    reducer: StateReducer<FriendsState, FriendsEvent, FriendsEffect>
) : BaseViewModel<FriendsState, FriendsEvent, FriendsEvent.FriendsViewEvent, FriendsEffect>(
    initialState = FriendsState.Idle,
    initialEffects = listOf(ObserveFriends, ObserveInvitations),
    schedulersProvider = schedulersProvider,
    reducer = reducer
) {

    init {
        disposables += friendsManager.results().subscribe()
    }

    override fun onEffect(effect: FriendsEffect) = when (effect) {

        ObserveFriends -> {
            disposables += friendsManager
                .friends()
                .subscribe {
                    onEvent(FriendsListReceived(it))
                }
        }

        ObserveInvitations -> {
            disposables += friendsManager
                .invitations()
                .subscribe {
                    onEvent(InvitationsListReceived(it))
                }
        }

        is SendIntent -> friendsManager.sendIntent(effect.intent)

        is TryConnect -> connectionManager.sendIntent(Connect(effect.id))

    }

}