package com.supnet.entry.login

sealed class LoginState {

    data class Idle(
        val email: String = "",
        val password: String = "",
        val isSignInEnabled: Boolean = false
    ) : LoginState()

    data class Loading(val backState: Idle) : LoginState()
}