package com.supnet.signaling.entities

enum class MessageStatus {
    SENDING,
    SENT,
    FAILED
}

data class Message(
    val author: String,
    val content: String,
    val status: MessageStatus
)