package com.supnet.domain.friends

sealed class FriendsManagerResult {

    sealed class InvitationResult : FriendsManagerResult() {

        object InvitationSend : InvitationResult()

        object InvitationFailure : InvitationResult()

    }

    sealed class AcceptInvitationResult : FriendsManagerResult() {

        object InvitationAccepted : AcceptInvitationResult()

        object AcceptInvitationFailure : AcceptInvitationResult()

    }

    sealed class RejectInvitationResult : FriendsManagerResult() {

        object InvitationRejected : RejectInvitationResult()

        object RejectInvitationFailure : RejectInvitationResult()

    }

}