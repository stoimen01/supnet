package com.supnet.data.credentials.api

data class SignResult(
    val id: Int,
    val username: String,
    val friends: List<Friend>,
    val gadgets: List<Gadget>
)

data class Friend(val name: String)

data class Gadget(val name: String, val owner: String)