package com.supnet.data.remote.rest

data class RegisterCredentials(
    val name: String,
    val email: String,
    val password: String
)