package com.supnet.indoor.friends

sealed class FriendsListItem {

    data class InvitationListItem(
        val id: Int,
        val initiatorName: String,
        val message: String
    ): FriendsListItem()

    data class FriendListItem(
        val id: Int,
        val friendName: String
    ): FriendsListItem()

}