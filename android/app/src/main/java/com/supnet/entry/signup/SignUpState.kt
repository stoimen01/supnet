package com.supnet.entry.signup

sealed class SignUpState {

    data class Idle(
        val username: String = "",
        val email: String = "",
        val password: String = "",
        val isCreateEnabled: Boolean = false
    ) : SignUpState()

    data class Loading(val backState: Idle) : SignUpState()
}