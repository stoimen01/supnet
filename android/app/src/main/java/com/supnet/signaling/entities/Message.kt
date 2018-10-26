package com.supnet.signaling.entities

data class Message(
    val author: User,
    val content: String
)