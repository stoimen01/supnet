package com.supnet.indoor.friends

import com.supnet.indoor.friends.FriendsListItem.*

sealed class FriendsState {

    object Idle : FriendsState()

    data class Ready(
        val friendItems: List<FriendListItem>,
        val invitationItems: List<InvitationListItem>,
        val listItems: List<FriendsListItem>
    ) : FriendsState()

}