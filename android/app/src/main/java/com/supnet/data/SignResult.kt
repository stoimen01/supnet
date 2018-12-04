package com.supnet.data

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

data class Friend(val name: String)

data class Gadget(val name: String, val owner: String)

data class InvitationRequest(
    val recipientName: String,
    val message: String
)

data class AcceptInvitationRequest(
    val invitationId: Int
)