package com.supnet.entry.signin

import com.supnet.data.connection.ConnectionState

sealed class SignInEvent {

    data class ConnectionStateChanged(val connState: ConnectionState) : SignInEvent()

    object OnSignInSuccess : SignInEvent()
    object OnSignInError : SignInEvent()

    sealed class SignInViewEvent : SignInEvent() {

        data class EmailChanged(val email: String) : SignInViewEvent()

        data class PasswordChanged(val password: String) : SignInViewEvent()

        object SignInClicked : SignInViewEvent()

        object CreateAccountClicked : SignInViewEvent()

    }

}