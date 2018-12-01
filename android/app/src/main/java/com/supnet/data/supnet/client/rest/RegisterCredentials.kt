package com.supnet.data.supnet.client.rest

data class RegisterCredentials(
    val name: String,
    val email: String,
    val password: String
)