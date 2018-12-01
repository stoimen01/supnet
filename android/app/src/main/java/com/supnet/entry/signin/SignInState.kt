package com.supnet.entry.signin

sealed class SignInState {

    data class Idle(
        val email: String = "",
        val password: String = "",
        val isSignInEnabled: Boolean = false
    ) : SignInState()

    data class Loading(val backState: Idle) : SignInState()
}