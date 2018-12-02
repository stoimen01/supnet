package com.supnet.data

data class SignResult(
    val id: Int,
    val token: String,
    val username: String,
    val friends: List<Friend>,
    val gadgets: List<Gadget>,
    val invitations: List<FriendshipInvitation>
)

data class Friend(val name: String)

data class Gadget(val name: String, val owner: String)

data class FriendshipInvitation(
    val initiatorName: String,
    val recipientName: String,
    val message: String
)