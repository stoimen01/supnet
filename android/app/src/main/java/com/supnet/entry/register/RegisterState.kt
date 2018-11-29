package com.supnet.entry.register

sealed class RegisterState {

    data class Idle(
        val username: String = "",
        val email: String = "",
        val password: String = "",
        val isCreateEnabled: Boolean = false
    ) : RegisterState()

    data class Loading(val backState: Idle) : RegisterState()
}