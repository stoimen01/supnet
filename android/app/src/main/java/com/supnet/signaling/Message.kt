package com.supnet.signaling

data class Message(
    val author: User,
    val content: String
)