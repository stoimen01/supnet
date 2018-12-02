package com.supnet.data.local

import com.supnet.data.Friend
import com.supnet.data.Gadget

data class UserInfo(
    val id: Int,
    val token: String,
    val name: String,
    val email: String,
    val password: String,
    val friends: List<Friend>,
    val gadgets: List<Gadget>
)