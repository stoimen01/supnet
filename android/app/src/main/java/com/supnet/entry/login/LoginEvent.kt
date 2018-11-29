package com.supnet.entry.login

import com.supnet.data.connection.ConnectionState

sealed class LoginEvent {

    data class ConnectionStateChanged(val connState: ConnectionState) : LoginEvent()

    object OnLoginSuccess : LoginEvent()
    object OnLoginError : LoginEvent()

    sealed class LoginViewEvent : LoginEvent() {

        data class EmailChanged(val email: String) : LoginViewEvent()

        data class PasswordChanged(val password: String) : LoginViewEvent()

        object SignInClicked : LoginViewEvent()

        object CreateAccountClicked : LoginViewEvent()

    }

}