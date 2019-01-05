package com.supnet.indoor.friends

import com.supnet.common.ReduceResult
import com.supnet.common.StateReducer
import com.supnet.domain.friends.FriendsManagerIntent
import com.supnet.domain.friends.FriendsManagerIntent.*
import com.supnet.indoor.friends.FriendsEffect.*
import com.supnet.indoor.friends.FriendsEvent.FriendsViewEvent.*
import com.supnet.indoor.friends.FriendsEvent.*
import com.supnet.indoor.friends.FriendsListItem.*
import com.supnet.indoor.friends.FriendsState.*

class FriendsReducer : StateReducer<FriendsState, FriendsEvent, FriendsEffect> {
    override fun reduce(
        lastResult: ReduceResult<FriendsState, FriendsEffect>,
        event: FriendsEvent
    ): ReduceResult<FriendsState, FriendsEffect> = when (val state = lastResult.state) {

        Idle -> when (event) {

            is FriendsListReceived -> {
                val friendItems = event.list.map { FriendListItem(it.id, it.name) }
                resultOf(Ready(friendItems, listOf(), friendItems))
            }

            is InvitationsListReceived -> {
                val invitationItems = event.list.map { InvitationListItem(it.id, it.senderName, it.message) }
                resultOf(Ready(listOf(), invitationItems, invitationItems))
            }

            else -> resultOf(state)
        }

        is Ready -> when (event) {

            is FriendsListReceived -> {
                val friendItems = event.list.map { FriendListItem(it.id, it.name) }
                resultOf(state.copy(
                    friendItems = friendItems,
                    listItems = state.invitationItems + friendItems
                ))
            }

            is InvitationsListReceived -> {
                val invitationItems = event.list.map { InvitationListItem(it.id, it.senderName, it.message) }
                resultOf(state.copy(
                    invitationItems = invitationItems,
                    listItems = state.friendItems + invitationItems
                ))
            }

            is OnRejectInvitation -> resultOf(state, SendIntent(RejectInvitation(event.id)))

            is OnAcceptInvitation -> resultOf(state, SendIntent(AcceptInvitation(event.id)))

            is OnConnect -> resultOf(state, TryConnect(event.id))

        }

    }
}