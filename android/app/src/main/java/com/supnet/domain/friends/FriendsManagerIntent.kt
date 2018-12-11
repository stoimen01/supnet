package com.supnet.domain.friends

sealed class FriendsManagerIntent {

    data class InvitationIntent(
        val recipient: String,
        val message: String
    ) : FriendsManagerIntent()

    data class AcceptInvitation(val id: Int) : FriendsManagerIntent()

    data class RejectInvitation(val id: Int) : FriendsManagerIntent()

    data class Connect(val id: Int) : FriendsManagerIntent()

}