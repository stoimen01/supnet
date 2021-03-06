package com.supnet.domain

import com.supnet.domain.entities.Friend
import com.supnet.domain.entities.Gadget

data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String
)

data class SignUpResponse(
    val id: Int,
    val token: String
)

data class SignInRequest(
    val email: String,
    val password: String
)

data class SignInResponse(
    val id: Int,
    val token: String,
    val username: String,
    val friends: List<Friend>,
    val gadgets: List<Gadget>
)

data class InvitationRequest(
    val recipientName: String,
    val message: String
)

data class AcceptInvitationRequest(
    val invitationId: Int
)

data class AcceptInvitationResponse(
    val friendId: Int,
    val friendName: String
)

data class RejectInvitationRequest(
    val invitationId: Int
)
