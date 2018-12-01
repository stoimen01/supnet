package com.supnet.data.supnet.store

import com.supnet.data.supnet.Friend
import com.supnet.data.supnet.Gadget

data class UserInfo(
    val id: Int,
    val token: String,
    val name: String,
    val email: String,
    val password: String,
    val friends: List<Friend>,
    val gadgets: List<Gadget>
)