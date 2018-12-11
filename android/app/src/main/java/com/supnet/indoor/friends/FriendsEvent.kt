package com.supnet.indoor.friends

import com.supnet.domain.entities.Friend
import com.supnet.domain.entities.Invitation

sealed class FriendsEvent {

    data class FriendsListReceived(val list: List<Friend>) : FriendsEvent()

    data class InvitationsListReceived(val list: List<Invitation>) : FriendsEvent()

    sealed class FriendsViewEvent : FriendsEvent() {

        data class OnRejectInvitation(val id: Int) : FriendsViewEvent()

        data class OnAcceptInvitation(val id: Int) : FriendsViewEvent()

        data class OnConnect(val id: Int) : FriendsViewEvent()

    }
}