package com.supnet.indoor.friends

import com.supnet.domain.friends.FriendsManagerIntent

sealed class FriendsEffect {

    object ObserveFriends : FriendsEffect()

    object ObserveInvitations : FriendsEffect()

    data class SendIntent(val intent: FriendsManagerIntent) : FriendsEffect()

}